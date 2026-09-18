package com.uberbackend.trip_service.entity;

public enum TripStatus {
    REQUESTED,
    SEARCHING_DRIVER,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    DRIVER_ARRIVED,
    TRIP_STARTED,
    TRIP_COMPLETED,
    CANCELLED,
    DRIVER_NOT_FOUND
}
