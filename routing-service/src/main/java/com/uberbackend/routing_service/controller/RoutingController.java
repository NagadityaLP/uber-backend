package com.uberbackend.routing_service.controller;

import com.uberbackend.routing_service.dto.RouteRequest;
import com.uberbackend.routing_service.dto.RouteResponse;
import com.uberbackend.routing_service.service.RoutingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> calculateRoute(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routingService.calculateRoute(request));
    }
}