package com.uberbackend.dispatch_service.dto;

import com.uberbackend.dispatch_service.entity.VehicleType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TripResponse {

    private Long id;

    private BigDecimal pickupLatitude;

    private BigDecimal pickupLongitude;

    private VehicleType vehicleType;
}