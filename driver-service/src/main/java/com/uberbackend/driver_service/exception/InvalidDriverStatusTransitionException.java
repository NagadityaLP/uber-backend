package com.uberbackend.driver_service.exception;

public class InvalidDriverStatusTransitionException extends RuntimeException {
    public InvalidDriverStatusTransitionException(String message) {
        super(message);
    }
}
