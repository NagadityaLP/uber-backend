package com.uberbackend.trip_service.dto;

import com.uberbackend.trip_service.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FareEstimateResponse {

    private VehicleType vehicleType;

    private BigDecimal distanceKm;

    private BigDecimal durationMinutes;

    private BigDecimal baseFare;

    private BigDecimal distanceFare;

    private BigDecimal timeFare;

    private BigDecimal surgeMultiplier;

    private BigDecimal totalFare;
}