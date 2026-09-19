package com.uberbackend.dispatch_service.dto;

import com.uberbackend.dispatch_service.entity.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AssignmentResponse {

    private Long id;
    private Long tripId;
    private String driverId;
    private AssignmentStatus status;
    private LocalDateTime offeredAt;
    private LocalDateTime acceptedAt;
}