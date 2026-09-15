package com.uberbackend.pricing_service.dto;

import com.uberbackend.pricing_service.entity.VehicleType;

import java.math.BigDecimal;

public class FareEstimateResponse {

    private VehicleType vehicleType;

    private BigDecimal distanceKm;

    private BigDecimal durationMinutes;

    private BigDecimal baseFare;

    private BigDecimal distanceFare;

    private BigDecimal timeFare;

    private BigDecimal surgeMultiplier;

    private BigDecimal totalFare;

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

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

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getDistanceFare() {
        return distanceFare;
    }

    public void setDistanceFare(BigDecimal distanceFare) {
        this.distanceFare = distanceFare;
    }

    public BigDecimal getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(BigDecimal timeFare) {
        this.timeFare = timeFare;
    }

    public BigDecimal getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public void setSurgeMultiplier(BigDecimal surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }
}