package com.uberbackend.trip_service.client;

import com.uberbackend.trip_service.dto.FareEstimateRequest;
import com.uberbackend.trip_service.dto.FareEstimateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PricingServiceClient {
    private final String pricingServiceBaseUrl = "http://localhost:8083/api/pricing";
    private final RestClient restClient;

    public PricingServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(pricingServiceBaseUrl).build();
    }

    public FareEstimateResponse calculateFare(FareEstimateRequest request) {

        return restClient.post()
                .uri("/estimate")
                .body(request)
                .retrieve()
                .body(FareEstimateResponse.class);
    }
}
