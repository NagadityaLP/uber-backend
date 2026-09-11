package com.uberbackend.driver_service.service;

import com.uberbackend.driver_service.dto.CreateDriverRequest;
import com.uberbackend.driver_service.dto.DriverResponse;
import com.uberbackend.driver_service.dto.UpdateDriverRequest;
import com.uberbackend.driver_service.dto.UpdateDriverStatusRequest;

import java.util.List;

public interface DriverService {

    DriverResponse createDriver(CreateDriverRequest request);

    DriverResponse getDriverById(Long id);

    List<DriverResponse> getAllDrivers();

    DriverResponse updateDriver(Long id, UpdateDriverRequest request);

    DriverResponse updateDriverStatus(Long id, UpdateDriverStatusRequest request);

    void deleteDriver(Long id);
}