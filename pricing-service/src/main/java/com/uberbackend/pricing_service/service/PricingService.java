package com.uberbackend.pricing_service.service;

import com.uberbackend.pricing_service.dto.FareEstimateRequest;
import com.uberbackend.pricing_service.dto.FareEstimateResponse;

public interface PricingService {
    FareEstimateResponse calculateFare(FareEstimateRequest request);
}
