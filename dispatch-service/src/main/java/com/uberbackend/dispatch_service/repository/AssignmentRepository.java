package com.uberbackend.dispatch_service.repository;

import com.uberbackend.dispatch_service.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTripId(Long tripId);
}
