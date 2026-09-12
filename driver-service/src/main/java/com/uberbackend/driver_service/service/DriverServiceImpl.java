package com.uberbackend.driver_service.service;

import com.uberbackend.driver_service.client.UserServiceClient;
import com.uberbackend.driver_service.dto.CreateDriverRequest;
import com.uberbackend.driver_service.dto.DriverResponse;
import com.uberbackend.driver_service.dto.UpdateDriverRequest;
import com.uberbackend.driver_service.dto.UpdateDriverStatusRequest;
import com.uberbackend.driver_service.entity.Driver;
import com.uberbackend.driver_service.entity.DriverStatus;
import com.uberbackend.driver_service.exception.DriverAlreadyExistsException;
import com.uberbackend.driver_service.exception.DriverNotFoundException;
import com.uberbackend.driver_service.exception.InvalidDriverStatusTransitionException;
import com.uberbackend.driver_service.exception.UserNotFoundException;
import com.uberbackend.driver_service.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserServiceClient userServiceClient;

    public DriverServiceImpl(DriverRepository driverRepository, UserServiceClient userServiceClient) {
        this.driverRepository = driverRepository;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public DriverResponse createDriver(CreateDriverRequest request) {
        // Implementation for creating a driver

        // Verify that the user exists in User Service
        if (!userServiceClient.userExists(request.getUserId())) {
            throw new UserNotFoundException(
                    "User not found with id: " + request.getUserId()
            );
        }

        if (driverRepository.existsByUserId(request.getUserId())) {
            throw new DriverAlreadyExistsException(
                    "Driver already exists for user ID: " + request.getUserId()
            );
        }

        Driver driver = new Driver();

        driver.setUserId(request.getUserId());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleType(request.getVehicleType());
        driver.setVehicleModel(request.getVehicleModel());

        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    @Override
    public DriverResponse getDriverById(Long id) {
        // Implementation for retrieving a driver by ID
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found with id: " + id));
        return mapToResponse(driver);
    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        // Implementation for retrieving all drivers
        return driverRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DriverResponse updateDriver(Long id, UpdateDriverRequest request) {
        // Implementation for updating a driver
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                "Driver not found with id: " + id
                        ));

        if (request.getLicenseNumber() != null) {
            driver.setLicenseNumber(request.getLicenseNumber());
        }

        if (request.getVehicleNumber() != null) {
            driver.setVehicleNumber(request.getVehicleNumber());
        }

        if (request.getVehicleType() != null) {
            driver.setVehicleType(request.getVehicleType());
        }

        if (request.getVehicleModel() != null) {
            driver.setVehicleModel(request.getVehicleModel());
        }

        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    @Override
    public void deleteDriver(Long id) {
        // Implementation for deleting a driver
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                "Driver not found with id: " + id
                        ));

        driverRepository.delete(driver);
    }

    @Override
    public DriverResponse updateDriverStatus(Long id, UpdateDriverStatusRequest request) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found with id: " + id));

        DriverStatus currentStatus = driver.getStatus();
        DriverStatus newStatus = request.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidDriverStatusTransitionException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        driver.setStatus(newStatus);

        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    @Override
    public List<Long> findAvailableDriverIds(List<Long> driverIds) {
        return driverRepository.findByIdInAndStatus(driverIds, DriverStatus.AVAILABLE)
                .stream().map(Driver::getId).toList();
    }

    private boolean isValidStatusTransition(DriverStatus currentStatus, DriverStatus newStatus) {
        return switch (currentStatus) {
            case OFFLINE ->
                    newStatus == DriverStatus.AVAILABLE;
            case AVAILABLE ->
                    newStatus == DriverStatus.ON_TRIP || newStatus == DriverStatus.OFFLINE;
            case ON_TRIP ->
                    newStatus == DriverStatus.AVAILABLE;
        };
    }

    private DriverResponse mapToResponse(Driver driver) {

        DriverResponse response = new DriverResponse();

        response.setId(driver.getId());
        response.setUserId(driver.getUserId());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setStatus(driver.getStatus());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setVehicleType(driver.getVehicleType());
        response.setVehicleModel(driver.getVehicleModel());
        response.setCreatedAt(driver.getCreatedAt());
        response.setUpdatedAt(driver.getUpdatedAt());

        return response;
    }
}
