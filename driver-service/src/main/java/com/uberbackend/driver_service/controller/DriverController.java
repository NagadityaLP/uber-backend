package com.uberbackend.driver_service.controller;

import com.uberbackend.driver_service.dto.CreateDriverRequest;
import com.uberbackend.driver_service.dto.UpdateDriverRequest;
import com.uberbackend.driver_service.dto.DriverResponse;
import com.uberbackend.driver_service.dto.UpdateDriverStatusRequest;
import com.uberbackend.driver_service.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/test")
    public String test() {
        return "Driver Controller is working";
    }

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        DriverResponse response = driverService.getDriverById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        List<DriverResponse> response = driverService.getAllDrivers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Long>> findAvailableDrivers(@RequestParam List<Long> ids) {
        List<Long> availableDriverIds = driverService.findAvailableDriverIds(ids);
        return ResponseEntity.ok(availableDriverIds);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver( @PathVariable Long id, @Valid @RequestBody UpdateDriverRequest request) {
        DriverResponse response = driverService.updateDriver(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponse> updateDriverStatus(@PathVariable Long id, @Valid @RequestBody UpdateDriverStatusRequest request) {
        DriverResponse response = driverService.updateDriverStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver( @PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
