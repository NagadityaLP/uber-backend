package com.uberbackend.routing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RouteResponse {

    private BigDecimal distanceKm;
    private BigDecimal durationMinutes;
}