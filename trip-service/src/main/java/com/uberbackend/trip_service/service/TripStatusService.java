package com.uberbackend.trip_service.service;

import com.uberbackend.trip_service.entity.Trip;
import com.uberbackend.trip_service.entity.TripStatus;
import org.springframework.stereotype.Service;

@Service
public class TripStatusService {

    public void moveTo(Trip trip, TripStatus newStatus) {

        TripStatus currentStatus = trip.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid trip status transition: " + currentStatus + " -> " + newStatus);
        }

        trip.setStatus(newStatus);
    }

    private boolean isValidTransition(TripStatus currentStatus, TripStatus newStatus) {

        return switch (currentStatus) {

            case REQUESTED ->
                    newStatus == TripStatus.SEARCHING_DRIVER
                            || newStatus == TripStatus.CANCELLED;

            case SEARCHING_DRIVER ->
                    newStatus == TripStatus.DRIVER_ASSIGNED
                            || newStatus == TripStatus.DRIVER_NOT_FOUND
                            || newStatus == TripStatus.CANCELLED;

            case DRIVER_ASSIGNED ->
                    newStatus == TripStatus.DRIVER_ARRIVING
                            || newStatus == TripStatus.CANCELLED;

            case DRIVER_ARRIVING ->
                    newStatus == TripStatus.DRIVER_ARRIVED
                            || newStatus == TripStatus.CANCELLED;

            case DRIVER_ARRIVED ->
                    newStatus == TripStatus.TRIP_STARTED
                            || newStatus == TripStatus.CANCELLED;

            case TRIP_STARTED ->
                    newStatus == TripStatus.TRIP_COMPLETED;

            case TRIP_COMPLETED,
                    CANCELLED,
                    DRIVER_NOT_FOUND ->
                    false;
        };
    }
}