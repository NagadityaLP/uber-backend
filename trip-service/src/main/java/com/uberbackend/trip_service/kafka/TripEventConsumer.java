package com.uberbackend.trip_service.kafka;

import com.uberbackend.trip_service.entity.ProcessedEvent;
import com.uberbackend.trip_service.entity.Trip;
import com.uberbackend.trip_service.entity.TripStatus;
import com.uberbackend.trip_service.event.DriverAssignedEvent;
import com.uberbackend.trip_service.repository.ProcessedEventRepository;
import com.uberbackend.trip_service.repository.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TripEventConsumer {
    private final TripRepository tripRepository;
    private final ProcessedEventRepository processedEventRepository;

    public TripEventConsumer(TripRepository tripRepository, ProcessedEventRepository processedEventRepository) {
        this.tripRepository = tripRepository;
        this.processedEventRepository = processedEventRepository;
    }

    // RetryableTopic annotation is used to configure retry behavior for the Kafka listener.
    // In this case, it specifies that the listener should attempt to process the message up to 3 times,
    // with a backoff delay of 2000 milliseconds (2 seconds) between attempts.
    @Transactional //Atomic transaction
    @RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000))
    @KafkaListener(topics = "trip-events")
    public void handleDriverAssigned(DriverAssignedEvent event) {

        // Durable Idempotency Mechanism
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Event with ID {} has already been processed. Skipping.", event.getEventId());
            return;
        }

        Trip trip = tripRepository.findById(event.getTripId())
                .orElseThrow(() -> new RuntimeException(
                        "Trip with ID " + event.getTripId() + " not found."
                ));
        trip.setDriverId(Long.valueOf(event.getDriverId()));
        trip.setStatus(TripStatus.DRIVER_ASSIGNED);

        tripRepository.save(trip);
        processedEventRepository.save(new ProcessedEvent(event.getEventId()));
    }
}
