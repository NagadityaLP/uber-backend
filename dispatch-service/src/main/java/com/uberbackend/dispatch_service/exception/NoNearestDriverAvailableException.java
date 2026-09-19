package com.uberbackend.dispatch_service.exception;

public class NoNearestDriverAvailableException extends RuntimeException {

    public NoNearestDriverAvailableException(String message) {
        super(message);
    }
}