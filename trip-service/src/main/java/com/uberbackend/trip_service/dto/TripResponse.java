package com.uberbackend.trip_service.dto;

import com.uberbackend.trip_service.entity.TripStatus;
import com.uberbackend.trip_service.entity.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TripResponse {

    private Long id;
    private Long userId;
    private Long driverId;

    private BigDecimal pickupLatitude;
    private BigDecimal pickupLongitude;

    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;

    private BigDecimal distanceKm;
    private BigDecimal estimatedDurationMinutes;

    private VehicleType vehicleType;

    private BigDecimal estimatedFare;
    private BigDecimal finalFare;

    private TripStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // getters and setters
}