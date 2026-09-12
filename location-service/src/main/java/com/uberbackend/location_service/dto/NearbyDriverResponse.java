package com.uberbackend.location_service.dto;

public class NearbyDriverResponse {

    private String driverId;
    private Double distanceInKm;

    public NearbyDriverResponse(String driverId, Double distanceInKm) {
        this.driverId = driverId;
        this.distanceInKm = distanceInKm;
    }

    public String getDriverId() {
        return driverId;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }
}
