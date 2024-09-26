package com.project.G1_T3.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message) {
        super(message);
    }
}