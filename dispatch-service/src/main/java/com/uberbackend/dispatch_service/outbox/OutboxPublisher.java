package com.uberbackend.dispatch_service.outbox;

import com.uberbackend.dispatch_service.entity.OutboxEvent;
import com.uberbackend.dispatch_service.event.DriverAssignedEvent;
import com.uberbackend.dispatch_service.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "trip-events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        System.out.println("OUTBOX POLLER RUNNING");

        var events = outboxEventRepository.findByPublishedFalseOrderByIdAsc();

        System.out.println("PENDING EVENTS: " + events.size());

        for (OutboxEvent event : events) {
            try {
                publishEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void publishEvent(OutboxEvent event) throws Exception {
        // We'll implement this part next.
        DriverAssignedEvent eventPayload = objectMapper.readValue(event.getPayload(), DriverAssignedEvent.class);
        kafkaTemplate.send(TOPIC, event.getAggregateId().toString(), eventPayload).get();

        event.setPublished(true);
        event.setPublishedAt(LocalDateTime.now());

        outboxEventRepository.save(event);
    }
}
