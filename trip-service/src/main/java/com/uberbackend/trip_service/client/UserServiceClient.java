package com.uberbackend.trip_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {
    private final String userServiceBaseUrl = "http://localhost:8080/api/users";
    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(userServiceBaseUrl).build();
    }

    public boolean userExists(Long userId) {
        try {
            Boolean response = restClient.get()
                                        .uri("/{userId}/exists", userId)
                                        .retrieve()
                                        .body(Boolean.class);
            return Boolean.TRUE.equals(response);
        } catch (Exception e) {
            return false;
        }
    }
}
