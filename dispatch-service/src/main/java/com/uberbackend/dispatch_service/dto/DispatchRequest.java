package com.uberbackend.dispatch_service.dto;

import com.uberbackend.dispatch_service.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DispatchRequest {

    private Long tripId;

    private BigDecimal pickupLatitude;

    private BigDecimal pickupLongitude;

    private VehicleType vehicleType;
}