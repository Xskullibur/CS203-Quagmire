package com.project.G1_T3.common.exception;

public class InvalidTokenException extends RuntimeException {
    private final String token;

    public InvalidTokenException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}