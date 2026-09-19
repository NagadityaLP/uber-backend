package com.uberbackend.dispatch_service.client;

import com.uberbackend.dispatch_service.dto.TripResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TripServiceClient {

    private final RestClient restClient;

    public TripServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8084/api/trips")
                .build();
    }

    public TripResponse getTrip(Long tripId) {
        return restClient.get()
                .uri("/{tripId}", tripId)
                .retrieve()
                .body(TripResponse.class);
    }
}