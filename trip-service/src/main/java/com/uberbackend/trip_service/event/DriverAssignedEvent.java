package com.uberbackend.trip_service.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DriverAssignedEvent {

    private UUID eventId;
    private String eventType;
    private Long tripId;
    private String driverId;
}