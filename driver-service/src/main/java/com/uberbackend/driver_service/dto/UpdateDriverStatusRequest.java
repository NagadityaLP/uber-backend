package com.uberbackend.driver_service.dto;

import com.uberbackend.driver_service.entity.DriverStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateDriverStatusRequest {

    @NotNull
    private DriverStatus status;

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}