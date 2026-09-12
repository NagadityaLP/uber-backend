package com.uberbackend.location_service.controller;

import com.uberbackend.location_service.dto.NearbyDriverResponse;
import com.uberbackend.location_service.dto.UpdateLocationRequest;
import com.uberbackend.location_service.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<Void> updateDriverLocation(@PathVariable Long driverId, @Valid @RequestBody UpdateLocationRequest request) {
        locationService.updateDriverLocation(driverId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyDriverResponse>> findNearbyDrivers(@RequestParam Double latitude,
            @RequestParam Double longitude, @RequestParam Double radius) {
        List<NearbyDriverResponse> drivers = locationService.findNearbyDrivers(latitude, longitude, radius);
        return ResponseEntity.ok(drivers);
    }
}
