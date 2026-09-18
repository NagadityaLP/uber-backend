package com.uberbackend.routing_service.exception;

public class RoutingProviderException extends RuntimeException {

    public RoutingProviderException(String message) {
        super(message);
    }

    public RoutingProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
