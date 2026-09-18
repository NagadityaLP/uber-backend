package com.uberbackend.trip_service.dto;

import com.uberbackend.trip_service.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FareEstimateRequest {

    private BigDecimal distanceKm;
    private BigDecimal durationMinutes;
    private VehicleType vehicleType;
}