package com.uberbackend.location_service.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DriverServiceClient {
    private final RestClient restClient;

    public DriverServiceClient() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:8081").build();
    }

    public List<Long> findAvailableDriverIds(List<Long> driverIds) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/drivers/available")
                        .queryParam("ids", driverIds)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Long>>() {});
    }
}
