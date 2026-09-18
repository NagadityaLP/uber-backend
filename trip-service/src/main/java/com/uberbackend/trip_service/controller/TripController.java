package com.uberbackend.trip_service.controller;

import com.uberbackend.trip_service.dto.CreateTripRequest;
import com.uberbackend.trip_service.dto.TripResponse;
import com.uberbackend.trip_service.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.ok(tripService.createTrip(request));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getTrip(tripId));
    }

    @PostMapping("/{tripId}/cancel")
    public ResponseEntity<Void> cancelTrip(@PathVariable Long tripId) {
        tripService.cancelTrip(tripId);
        return ResponseEntity.noContent().build();
    }
}
