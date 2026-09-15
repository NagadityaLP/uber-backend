package com.uberbackend.pricing_service.controller;

import com.uberbackend.pricing_service.dto.FareEstimateRequest;
import com.uberbackend.pricing_service.dto.FareEstimateResponse;
import com.uberbackend.pricing_service.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/estimate")
    public ResponseEntity<FareEstimateResponse> calculateFare(@Valid @RequestBody FareEstimateRequest request) {
        FareEstimateResponse response = pricingService.calculateFare(request);
        return ResponseEntity.ok(response);
    }
}
