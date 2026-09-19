package com.uberbackend.dispatch_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NearbyDriverResponse {

    private String driverId;
    private Double distance;
}