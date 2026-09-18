package com.uberbackend.routing_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentCoordinatesValidator.class)
@Documented
public @interface DifferentCoordinates {
    String message() default "Origin and destination coordinates must be different";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
