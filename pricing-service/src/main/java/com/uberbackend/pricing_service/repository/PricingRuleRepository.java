package com.uberbackend.pricing_service.repository;

import com.uberbackend.pricing_service.entity.PricingRule;
import com.uberbackend.pricing_service.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    Optional<PricingRule> findByVehicleType(VehicleType vehicleType);
}
