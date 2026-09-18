package com.uberbackend.routing_service.validation;

import com.uberbackend.routing_service.dto.RouteRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentCoordinatesValidator implements ConstraintValidator<DifferentCoordinates, RouteRequest> {

    @Override
    public boolean isValid(RouteRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Consider null as valid, use @NotNull for null checks
        }
        if (request.getOriginLatitude() == null || request.getOriginLongitude() == null
                || request.getDestinationLatitude() == null || request.getDestinationLongitude() == null) {
            return true;
        }

        return !(request.getOriginLatitude().compareTo(request.getDestinationLatitude()) == 0
                && request.getOriginLongitude().compareTo(request.getDestinationLongitude()) == 0);
    }
}
