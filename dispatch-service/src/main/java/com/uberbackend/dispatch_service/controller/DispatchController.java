package com.uberbackend.dispatch_service.controller;

import com.uberbackend.dispatch_service.dto.AssignmentActionRequest;
import com.uberbackend.dispatch_service.dto.AssignmentResponse;
import com.uberbackend.dispatch_service.dto.DispatchRequest;
import com.uberbackend.dispatch_service.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {
    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping
    public ResponseEntity<AssignmentResponse> dispatchTrip(@RequestBody DispatchRequest request) {
        AssignmentResponse assignmentResponse = dispatchService.dispatchTrip(request);
        return ResponseEntity.ok(assignmentResponse);
    }

    @PostMapping("/assignments/{assignmentId}/accept")
    public ResponseEntity<AssignmentResponse> acceptAssignment(@PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentActionRequest request) {
        AssignmentResponse response = dispatchService.acceptAssignment(assignmentId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assignments/{assignmentId}/reject")
    public ResponseEntity<AssignmentResponse> rejectAssignment(@PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentActionRequest request) {
        AssignmentResponse response = dispatchService.rejectAssignment(assignmentId, request);
        return ResponseEntity.ok(response);
    }
}
