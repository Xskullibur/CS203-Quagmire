package com.project.G1_T3.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfileAlreadyExistException extends RuntimeException {
    public ProfileAlreadyExistException(String message) {
        super(message);
    }
}