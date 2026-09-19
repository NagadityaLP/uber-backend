package com.uberbackend.dispatch_service.service;

import com.uberbackend.dispatch_service.dto.AssignmentActionRequest;
import com.uberbackend.dispatch_service.dto.AssignmentResponse;
import com.uberbackend.dispatch_service.dto.DispatchRequest;

public interface DispatchService {
    AssignmentResponse dispatchTrip(DispatchRequest request);
    AssignmentResponse acceptAssignment(Long assignmentId, AssignmentActionRequest request);
    AssignmentResponse rejectAssignment(Long assignmentId, AssignmentActionRequest request);
}
