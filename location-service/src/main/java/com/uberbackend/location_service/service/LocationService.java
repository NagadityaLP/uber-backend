package com.uberbackend.location_service.service;

import com.uberbackend.location_service.dto.NearbyDriverResponse;
import com.uberbackend.location_service.dto.UpdateLocationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface LocationService {
    void updateDriverLocation(Long driverId, UpdateLocationRequest request);

    List<NearbyDriverResponse> findNearbyDrivers(Double latitude, Double longitude, Double radiusInKm);
}
