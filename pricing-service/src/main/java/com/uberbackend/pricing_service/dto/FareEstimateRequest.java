package com.uberbackend.pricing_service.dto;

import com.uberbackend.pricing_service.entity.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FareEstimateRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal distanceKm;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal durationMinutes;

    @NotNull
    private VehicleType vehicleType;

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(BigDecimal durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}