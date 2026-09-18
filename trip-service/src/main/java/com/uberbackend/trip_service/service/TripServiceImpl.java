package com.uberbackend.trip_service.service;

import com.uberbackend.trip_service.client.PricingServiceClient;
import com.uberbackend.trip_service.client.RoutingServiceClient;
import com.uberbackend.trip_service.client.UserServiceClient;
import com.uberbackend.trip_service.dto.*;
import com.uberbackend.trip_service.entity.Trip;
import com.uberbackend.trip_service.entity.TripStatus;
import com.uberbackend.trip_service.exception.TripNotFoundException;
import com.uberbackend.trip_service.exception.UserNotFoundException;
import com.uberbackend.trip_service.repository.TripRepository;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripStatusService tripStatusService;
    private final UserServiceClient userServiceClient;
    private final RoutingServiceClient routingServiceClient;
    private final PricingServiceClient pricingServiceClient;

    public TripServiceImpl(TripRepository tripRepository, TripStatusService tripStatusService,
                           UserServiceClient userServiceClient,
                           RoutingServiceClient routingServiceClient,
                           PricingServiceClient pricingServiceClient) {
        this.tripRepository = tripRepository;
        this.tripStatusService = tripStatusService;
        this.userServiceClient = userServiceClient;
        this.routingServiceClient = routingServiceClient;
        this.pricingServiceClient = pricingServiceClient;
    }

    @Override
    public TripResponse createTrip(CreateTripRequest request) {
        // Implement the logic to create a trip

        // Validate if the user exists by calling the User Service
        if (!userServiceClient.userExists(request.getUserId())) {
            throw new UserNotFoundException("User with ID " + request.getUserId() + " not found.");
        }

        // Calculate the route and get distance and duration from the Routing Service
        RouteRequest routeRequest = new RouteRequest(
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDestinationLatitude(),
                request.getDestinationLongitude()
        );

        RouteResponse routeResponse = routingServiceClient.calculateRoute(routeRequest);

        // Calculate the estimated fare using the Pricing Service
        FareEstimateRequest fareRequest = new FareEstimateRequest(
                routeResponse.getDistanceKm(),
                routeResponse.getDurationMinutes(),
                request.getVehicleType()
        );

        FareEstimateResponse fareResponse = pricingServiceClient.calculateFare(fareRequest);

        Trip trip = new Trip();

        trip.setUserId(request.getUserId());

        trip.setPickupLatitude(request.getPickupLatitude());
        trip.setPickupLongitude(request.getPickupLongitude());

        trip.setDestinationLatitude(request.getDestinationLatitude());
        trip.setDestinationLongitude(request.getDestinationLongitude());

        trip.setDistanceKm(routeResponse.getDistanceKm());
        trip.setEstimatedDurationMinutes(routeResponse.getDurationMinutes());
        trip.setEstimatedFare(fareResponse.getTotalFare());

        trip.setVehicleType(request.getVehicleType());

        trip.setStatus(TripStatus.REQUESTED);

        Trip savedTrip = tripRepository.save(trip);

        return mapToResponse(savedTrip);
    }

    @Override
    public TripResponse getTrip(Long tripId) {
        // Implement the logic to get a trip by ID
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        return mapToResponse(trip);
    }

    @Override
    public void cancelTrip(Long tripId) {
        // Implement the logic to cancel a trip by ID
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        tripStatusService.moveTo(trip, TripStatus.CANCELLED);

        tripRepository.save(trip);
    }

    private TripResponse mapToResponse(Trip trip) {

        TripResponse response = new TripResponse();

        response.setId(trip.getId());
        response.setUserId(trip.getUserId());
        response.setDriverId(trip.getDriverId());

        response.setPickupLatitude(trip.getPickupLatitude());
        response.setPickupLongitude(trip.getPickupLongitude());

        response.setDestinationLatitude(trip.getDestinationLatitude());
        response.setDestinationLongitude(trip.getDestinationLongitude());

        response.setDistanceKm(trip.getDistanceKm());
        response.setEstimatedDurationMinutes(trip.getEstimatedDurationMinutes());

        response.setVehicleType(trip.getVehicleType());

        response.setEstimatedFare(trip.getEstimatedFare());
        response.setFinalFare(trip.getFinalFare());

        response.setStatus(trip.getStatus());

        response.setCreatedAt(trip.getCreatedAt());
        response.setStartedAt(trip.getStartedAt());
        response.setCompletedAt(trip.getCompletedAt());

        return response;
    }
}
