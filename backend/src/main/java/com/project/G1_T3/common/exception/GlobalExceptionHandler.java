package com.project.G1_T3.common.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DESC = "description";

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The username is already taken");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUseException(EmailAlreadyInUseException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The email is already in use");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid Argument");

        StringBuilder validationErrors = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrors.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        errorDetail.setProperty(DESC, validationErrors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleSecurityException(Exception exception) {

        ProblemDetail errorDetail = null;

        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "The username or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "The account is locked");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "You are not authorized to access this resource");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "The JWT token has expired");
            errorDetail.setProperty("errorCode", "TOKEN_EXPIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof JwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "Invalid JWT token");
            errorDetail.setProperty("errorCode", "INVALID_TOKEN");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof InvalidTokenException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "Invalid token");
            errorDetail.setProperty("errorCode", "INVALID_TOKEN");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        // Default case for unknown exceptions
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty(DESC, "Unknown internal server error.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
    }
}