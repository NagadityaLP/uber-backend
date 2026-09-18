package com.uberbackend.trip_service.client;

import com.uberbackend.trip_service.dto.RouteRequest;
import com.uberbackend.trip_service.dto.RouteResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RoutingServiceClient {
    private final RestClient restClient;

    public RoutingServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8085/api/routes").build();
    }

    public RouteResponse calculateRoute(RouteRequest request) {
        return restClient.post().body(request).retrieve().body(RouteResponse.class);
    }
}
