package com.uberbackend.trip_service.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DriverAssignedEvent {

    private String eventType;
    private Long tripId;
    private String driverId;
}