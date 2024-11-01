package com.project.G1_T3.common.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}