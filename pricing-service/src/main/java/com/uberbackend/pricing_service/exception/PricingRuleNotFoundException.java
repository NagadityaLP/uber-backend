package com.uberbackend.pricing_service.exception;

public class PricingRuleNotFoundException extends RuntimeException {

    public PricingRuleNotFoundException(String message) {
        super(message);
    }
}