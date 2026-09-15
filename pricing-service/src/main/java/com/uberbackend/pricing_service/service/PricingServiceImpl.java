package com.uberbackend.pricing_service.service;

import com.uberbackend.pricing_service.dto.FareEstimateRequest;
import com.uberbackend.pricing_service.dto.FareEstimateResponse;
import com.uberbackend.pricing_service.entity.PricingRule;
import com.uberbackend.pricing_service.exception.PricingRuleNotFoundException;
import com.uberbackend.pricing_service.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingServiceImpl implements PricingService {

    private final PricingRuleRepository pricingRuleRepository;

    public PricingServiceImpl(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    @Override
    public FareEstimateResponse calculateFare(FareEstimateRequest request) {
        PricingRule pricingRule = pricingRuleRepository.findByVehicleType(request.getVehicleType())
                .orElseThrow(() -> new PricingRuleNotFoundException("Pricing rule not found for vehicle type: " + request.getVehicleType()));

        BigDecimal distanceFare = request.getDistanceKm().multiply(pricingRule.getPerKmRate());
        BigDecimal timeFare = request.getDurationMinutes().multiply(pricingRule.getPerMinuteRate());

        BigDecimal subtotal = pricingRule.getBaseFare().add(distanceFare).add(timeFare);

        BigDecimal totalFare = subtotal.multiply(pricingRule.getSurgeMultiplier()).setScale(2, RoundingMode.HALF_UP);

        FareEstimateResponse response = new FareEstimateResponse();

        response.setVehicleType(request.getVehicleType());
        response.setDistanceKm(request.getDistanceKm());
        response.setDurationMinutes(request.getDurationMinutes());
        response.setBaseFare(pricingRule.getBaseFare());
        response.setDistanceFare(distanceFare.setScale(2, RoundingMode.HALF_UP));
        response.setTimeFare(timeFare.setScale(2, RoundingMode.HALF_UP));
        response.setSurgeMultiplier(pricingRule.getSurgeMultiplier());
        response.setTotalFare(totalFare);

        return response;
    }
}
