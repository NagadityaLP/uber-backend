package com.uberbackend.driver_service.repository;

import com.uberbackend.driver_service.entity.Driver;
import com.uberbackend.driver_service.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
    List<Driver> findByIdInAndStatus(List<Long> ids, DriverStatus status);
}
