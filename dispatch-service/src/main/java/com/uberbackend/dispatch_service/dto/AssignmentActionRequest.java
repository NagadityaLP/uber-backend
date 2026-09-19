package com.uberbackend.dispatch_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentActionRequest {

    @NotBlank
    private String driverId;
}