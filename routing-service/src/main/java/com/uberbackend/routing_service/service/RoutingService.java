package com.uberbackend.routing_service.service;

import com.uberbackend.routing_service.dto.RouteRequest;
import com.uberbackend.routing_service.dto.RouteResponse;
import com.uberbackend.routing_service.provider.RoutingProvider;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    private final RoutingProvider routingProvider;

    public RoutingService(RoutingProvider routingProvider) {
        this.routingProvider = routingProvider;
    }

    public RouteResponse calculateRoute(RouteRequest request) {
        return routingProvider.calculateRoute(request);
    }
}