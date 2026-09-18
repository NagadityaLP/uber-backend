package com.uberbackend.routing_service.provider;

import com.uberbackend.routing_service.dto.RouteRequest;
import com.uberbackend.routing_service.dto.RouteResponse;
import com.uberbackend.routing_service.exception.RouteNotFoundException;
import com.uberbackend.routing_service.exception.RoutingProviderException;
import com.uberbackend.routing_service.provider.dto.GraphHopperResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class GraphHopperRoutingProvider implements RoutingProvider {

    private final RestClient restClient;
    private final String apiKey;

    public GraphHopperRoutingProvider(RestClient.Builder restClientBuilder, @Value("${graphhopper.api-key}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl("https://graphhopper.com/api/1").build();
        this.apiKey = apiKey;
    }

    @Override
    public RouteResponse calculateRoute(RouteRequest request) {
        // Implement the logic to call GraphHopper API and calculate the route
        try {
            GraphHopperResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/route")
                            .queryParam("point", request.getOriginLatitude() + "," + request.getOriginLongitude())
                            .queryParam("point", request.getDestinationLatitude() + "," + request.getDestinationLongitude())
                            .queryParam("profile", "car")
                            .queryParam("calc_points", false)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(GraphHopperResponse.class);

            if (response == null || response.getPaths() == null || response.getPaths().isEmpty()) {
                throw new RouteNotFoundException("No route found");
            }

            GraphHopperResponse.Path path = response.getPaths().getFirst();

            BigDecimal distanceKm = BigDecimal.valueOf(path.getDistance())
                    .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

            BigDecimal durationMinutes = BigDecimal.valueOf(path.getTime())
                    .divide(BigDecimal.valueOf(60000), 2, RoundingMode.HALF_UP);

            return new RouteResponse(distanceKm, durationMinutes);
        }
        catch (ResourceAccessException ex) {
            throw new RoutingProviderException("Routing provider is unavailable", ex);
        }
        catch (RestClientException ex) {
            throw new RoutingProviderException("Routing provider returned an error", ex);
        }
    }
}
