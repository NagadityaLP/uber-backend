package com.uberbackend.dispatch_service.service;

import com.uberbackend.dispatch_service.client.LocationServiceClient;
import com.uberbackend.dispatch_service.client.TripServiceClient;
import com.uberbackend.dispatch_service.dto.*;
import com.uberbackend.dispatch_service.entity.Assignment;
import com.uberbackend.dispatch_service.entity.AssignmentStatus;
import com.uberbackend.dispatch_service.entity.OutboxEvent;
import com.uberbackend.dispatch_service.event.DriverAssignedEvent;
import com.uberbackend.dispatch_service.exception.NoNearestDriverAvailableException;
import com.uberbackend.dispatch_service.kafka.TripEventProducer;
import com.uberbackend.dispatch_service.repository.AssignmentRepository;
import com.uberbackend.dispatch_service.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DispatchServiceImpl implements DispatchService {

    private final LocationServiceClient locationServiceClient;
    private final TripServiceClient tripServiceClient;
    private final AssignmentRepository assignmentRepository;
    private final TripEventProducer tripEventProducer;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Value("${dispatch.search-radius-km}")
    private Double searchRadiusKm;

    public DispatchServiceImpl(LocationServiceClient locationServiceClient,
                               TripServiceClient tripServiceClient,
                               AssignmentRepository assignmentRepository,
                               OutboxEventRepository outboxEventRepository,
                               TripEventProducer tripEventProducer,
                               ObjectMapper objectMapper) {
        this.locationServiceClient = locationServiceClient;
        this.tripServiceClient = tripServiceClient;
        this.assignmentRepository = assignmentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.tripEventProducer = tripEventProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public AssignmentResponse dispatchTrip(DispatchRequest request) {
        // Find nearby available drivers using the LocationServiceClient
        List<NearbyDriverResponse> nearbyDrivers = locationServiceClient.findNearbyAvailableDriverIds(
                request.getPickupLatitude().doubleValue(),
                request.getPickupLongitude().doubleValue(),
                searchRadiusKm // radius in kilometers
        );

        if (nearbyDrivers.isEmpty()) {
            throw new NoNearestDriverAvailableException("No nearest available driver found for the trip.");
        }

        List<String> attemptedDriverIds = getAttemptedDriverIds(request.getTripId());

        NearbyDriverResponse selectedDriver = nearbyDrivers.stream()
                .filter(driver ->
                        !attemptedDriverIds.contains(driver.getDriverId()))
                .findFirst()
                .orElseThrow(() -> new NoNearestDriverAvailableException(
                        "No nearest available driver found for the trip."
                ));

        // Create and save the assignment
        Assignment assignment = new Assignment();
        assignment.setTripId(request.getTripId());
        assignment.setDriverId(selectedDriver.getDriverId());
        assignment.setStatus(AssignmentStatus.OFFERED);
        assignment.setOfferedAt(LocalDateTime.now());

        Assignment savedAssignment = assignmentRepository.save(assignment);

        return new AssignmentResponse(
                savedAssignment.getId(),
                savedAssignment.getTripId(),
                savedAssignment.getDriverId(),
                savedAssignment.getStatus(),
                savedAssignment.getOfferedAt(),
                savedAssignment.getAcceptedAt()
        );
    }

    @Transactional
    @Override
    public AssignmentResponse acceptAssignment(Long assignmentId, AssignmentActionRequest request) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Assignment with ID " + assignmentId + " not found."
                ));

        if (assignment.getStatus() != AssignmentStatus.OFFERED) {
            throw new IllegalStateException(
                    "Assignment is not available for acceptance."
            );
        }

        if (!assignment.getDriverId().equals(request.getDriverId())) {
            throw new IllegalStateException(
                    "Driver is not assigned to this assignment."
            );
        }

        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());

        Assignment savedAssignment = assignmentRepository.save(assignment);

        DriverAssignedEvent event = new DriverAssignedEvent(
                UUID.randomUUID(),
                "DriverAssigned",
                savedAssignment.getTripId(),
                savedAssignment.getDriverId()
        );

        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize DriverAssigned event", e);
        }

        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventType("DriverAssigned");
        outboxEvent.setAggregateType("TRIP");
        outboxEvent.setAggregateId(assignment.getTripId());
        outboxEvent.setPayload(payload);

        outboxEventRepository.save(outboxEvent);

        return new AssignmentResponse(
                savedAssignment.getId(),
                savedAssignment.getTripId(),
                savedAssignment.getDriverId(),
                savedAssignment.getStatus(),
                savedAssignment.getOfferedAt(),
                savedAssignment.getAcceptedAt()
        );
    }

    @Override
    public AssignmentResponse rejectAssignment(Long assignmentId, AssignmentActionRequest request) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Assignment with ID " + assignmentId + " not found."
                ));

        if (assignment.getStatus() != AssignmentStatus.OFFERED) {
            throw new IllegalStateException(
                    "Assignment is not available for rejection."
            );
        }

        if (!assignment.getDriverId().equals(request.getDriverId())) {
            throw new IllegalStateException(
                    "Driver is not assigned to this assignment."
            );
        }

        assignment.setStatus(AssignmentStatus.REJECTED);

        assignmentRepository.save(assignment);

        TripResponse trip = tripServiceClient.getTrip(assignment.getTripId());

        List<NearbyDriverResponse> nearbyDrivers =
                locationServiceClient.findNearbyAvailableDriverIds(
                        trip.getPickupLatitude().doubleValue(),
                        trip.getPickupLongitude().doubleValue(),
                        searchRadiusKm
                );

        List<String> attemptedDriverIds = getAttemptedDriverIds(assignment.getTripId());

        NearbyDriverResponse nextDriver = nearbyDrivers.stream()
                .filter(driver ->
                        !attemptedDriverIds.contains(driver.getDriverId()))
                .findFirst()
                .orElseThrow(() -> new NoNearestDriverAvailableException(
                        "No more available drivers found for the trip."
                ));

        Assignment nextAssignment = new Assignment();

        nextAssignment.setTripId(assignment.getTripId());
        nextAssignment.setDriverId(nextDriver.getDriverId());
        nextAssignment.setStatus(AssignmentStatus.OFFERED);
        nextAssignment.setOfferedAt(LocalDateTime.now());

        Assignment savedAssignment = assignmentRepository.save(nextAssignment);

        return new AssignmentResponse(
                savedAssignment.getId(),
                savedAssignment.getTripId(),
                savedAssignment.getDriverId(),
                savedAssignment.getStatus(),
                savedAssignment.getOfferedAt(),
                savedAssignment.getAcceptedAt()
        );
    }

    private List<String> getAttemptedDriverIds(Long tripId) {

        return assignmentRepository.findByTripId(tripId)
                .stream()
                .map(Assignment::getDriverId)
                .toList();
    }
}
