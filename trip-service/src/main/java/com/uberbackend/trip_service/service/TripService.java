package com.uberbackend.trip_service.service;

import com.uberbackend.trip_service.dto.CreateTripRequest;
import com.uberbackend.trip_service.dto.TripResponse;

public interface TripService {

    TripResponse createTrip(CreateTripRequest request);

    TripResponse getTrip(Long tripId);

    void cancelTrip(Long tripId);
}