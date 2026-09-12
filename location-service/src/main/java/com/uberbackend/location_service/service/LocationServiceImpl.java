package com.uberbackend.location_service.service;

import com.uberbackend.location_service.client.DriverServiceClient;
import com.uberbackend.location_service.dto.NearbyDriverResponse;
import com.uberbackend.location_service.dto.UpdateLocationRequest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LocationServiceImpl implements LocationService {
    private static final String DRIVER_LOCATIONS_KEY = "driver_locations";

    private final DriverServiceClient driverServiceClient;

    private final RedisTemplate<String, String> redisTemplate;

    public LocationServiceImpl(RedisTemplate<String, String> redisTemplate, DriverServiceClient driverServiceClient) {
        this.redisTemplate = redisTemplate;
        this.driverServiceClient = driverServiceClient;
    }

    @Override
    public void updateDriverLocation(Long driverId, UpdateLocationRequest request) {
        redisTemplate.opsForGeo().add(DRIVER_LOCATIONS_KEY,
                new Point(request.getLongitude(), request.getLatitude()), driverId.toString());
    }

    @Override
    public List<NearbyDriverResponse> findNearbyDrivers(Double latitude, Double longitude, Double radiusInKm) {
        Point searchPoint = new Point(longitude, latitude);
        Distance radius = new Distance(radiusInKm, Metrics.KILOMETERS);
        Circle searchArea = new Circle(searchPoint, radius);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                        .newGeoRadiusArgs().includeDistance().sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_LOCATIONS_KEY, searchArea, args);

        List<Long> nearbyDriverIds = results.getContent().stream()
                        .map(result -> Long.valueOf(result.getContent().getName()))
                        .toList();

        if (nearbyDriverIds.isEmpty()) {
            return List.of();
        }

        Set<Long> availableDriverIds = new HashSet<>(driverServiceClient.findAvailableDriverIds(nearbyDriverIds));

        return results.getContent().stream()
                .filter(res -> availableDriverIds.contains(Long.valueOf(res.getContent().getName())))
                .map(result -> {
                    double distance = result.getDistance().getValue();

                    if (distance < 0.001)
                        distance = 0.0;

                    return new NearbyDriverResponse(result.getContent().getName(), distance);
                }).toList();
    }
}
