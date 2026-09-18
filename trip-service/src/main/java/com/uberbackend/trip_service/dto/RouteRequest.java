package com.uberbackend.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RouteRequest {

    private BigDecimal originLatitude;
    private BigDecimal originLongitude;
    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;
}