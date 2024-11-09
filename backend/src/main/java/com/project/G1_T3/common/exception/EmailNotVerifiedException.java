package com.project.G1_T3.common.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailNotVerifiedException extends RuntimeException {

    public EmailNotVerifiedException(String username) {
        super(String.format("Email not verified for user: %s", username));
    }
    
    public EmailNotVerifiedException(String username, String message) {
        super(String.format("Email not verified for user: %s. %s", username, message));
    }
}