package com.uberbackend.routing_service.provider;

import com.uberbackend.routing_service.dto.RouteRequest;
import com.uberbackend.routing_service.dto.RouteResponse;

public interface RoutingProvider {

    RouteResponse calculateRoute(RouteRequest request);
}