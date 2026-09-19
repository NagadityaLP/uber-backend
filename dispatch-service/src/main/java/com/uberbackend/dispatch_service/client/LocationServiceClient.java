package com.uberbackend.dispatch_service.client;

import com.uberbackend.dispatch_service.dto.NearbyDriverResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class LocationServiceClient {

    private final String locationServiceBaseUrl = "http://localhost:8082/api/locations";
    private final RestClient restClient;

    public LocationServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(locationServiceBaseUrl).build();
    }

    public List<NearbyDriverResponse> findNearbyAvailableDriverIds(Double latitude, Double longitude,
                                                                   Double radius) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/nearby")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("radius", radius)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<NearbyDriverResponse>>() {});
    }
}
