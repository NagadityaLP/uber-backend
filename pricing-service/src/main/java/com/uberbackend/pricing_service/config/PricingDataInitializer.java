package com.uberbackend.pricing_service.config;

import com.uberbackend.pricing_service.entity.PricingRule;
import com.uberbackend.pricing_service.entity.VehicleType;
import com.uberbackend.pricing_service.repository.PricingRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class PricingDataInitializer {

    @Bean
    CommandLineRunner loadPricingRules(PricingRuleRepository pricingRuleRepository) {
        return args -> {
            createIfNotExists(pricingRuleRepository, VehicleType.SEDAN, "50.00", "15.00", "2.00", "1.00");
            createIfNotExists(pricingRuleRepository, VehicleType.SUV, "70.00", "20.00", "3.00", "1.00");
            createIfNotExists(pricingRuleRepository, VehicleType.HATCHBACK, "40.00","12.00", "1.50", "1.00");
            createIfNotExists(pricingRuleRepository, VehicleType.AUTO, "30.00", "10.00", "1.50", "1.00");
        };
    }

    private void createIfNotExists(PricingRuleRepository repository, VehicleType vehicleType,
            String baseFare, String perKmRate, String perMinuteRate, String surgeMultiplier) {

        if (repository.findByVehicleType(vehicleType).isEmpty()) {

            PricingRule rule = new PricingRule();

            rule.setVehicleType(vehicleType);
            rule.setBaseFare(new BigDecimal(baseFare));
            rule.setPerKmRate(new BigDecimal(perKmRate));
            rule.setPerMinuteRate(new BigDecimal(perMinuteRate));
            rule.setSurgeMultiplier(new BigDecimal(surgeMultiplier));

            repository.save(rule);
        }
    }
}
