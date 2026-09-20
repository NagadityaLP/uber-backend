package com.uberbackend.dispatch_service.kafka;

import com.uberbackend.dispatch_service.event.DriverAssignedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TripEventProducer {
    private static final String TOPIC = "trip-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TripEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDriverAssigned(DriverAssignedEvent event) {
        kafkaTemplate.send(TOPIC, event.getTripId().toString(), event);
    }
}
