package com.uberbackend.trip_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long driverId;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal pickupLatitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal pickupLongitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal destinationLatitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal destinationLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalFare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = TripStatus.REQUESTED;
        }
    }
}