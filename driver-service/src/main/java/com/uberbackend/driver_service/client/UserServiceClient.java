package com.uberbackend.driver_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public boolean userExists(Long userId) {
        try {
            restClient.get()
                    .uri("api/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
