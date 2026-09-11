package com.uberbackend.driver_service.repository;

import com.uberbackend.driver_service.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
}
