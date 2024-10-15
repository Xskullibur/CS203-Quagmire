package com.project.G1_T3.common.exception;

public class LocationServiceException extends RuntimeException {
    public LocationServiceException(String message) {
        super(message);
    }

    public LocationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}