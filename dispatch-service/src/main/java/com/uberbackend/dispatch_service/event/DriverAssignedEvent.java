package com.uberbackend.dispatch_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverAssignedEvent {

    private String eventType;
    private Long tripId;
    private String driverId;
}