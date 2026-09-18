package com.uberbackend.trip_service.repository;

import com.uberbackend.trip_service.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
