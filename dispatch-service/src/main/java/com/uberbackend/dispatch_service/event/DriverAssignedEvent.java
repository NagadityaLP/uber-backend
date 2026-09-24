package com.uberbackend.dispatch_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DriverAssignedEvent {
    private UUID eventId;
    private String eventType;
    private Long tripId;
    private String driverId;
}