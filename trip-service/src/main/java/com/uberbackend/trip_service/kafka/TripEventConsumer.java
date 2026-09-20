package com.uberbackend.trip_service.kafka;

import com.uberbackend.trip_service.event.DriverAssignedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TripEventConsumer {
    @KafkaListener(topics = "trip-events")
    public void handleDriverAssigned(DriverAssignedEvent event) {
        System.out.println("Received DriverAssigned event: tripId="
                        + event.getTripId()
                        + ", driverId=" + event.getDriverId());
    }
}
