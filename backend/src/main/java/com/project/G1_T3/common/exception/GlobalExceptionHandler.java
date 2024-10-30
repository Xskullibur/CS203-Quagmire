package com.project.G1_T3.common.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String DESC = "description";
    private static final String ERROR_CODE = "code";

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The username is already taken");
        errorDetail.setProperty(ERROR_CODE, "DUPLICATE_ENTRY");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUseException(EmailAlreadyInUseException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The email is already in use");
        errorDetail.setProperty(ERROR_CODE, "DUPLICATE_ENTRY");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "Invalid argument provided");
        errorDetail.setProperty(ERROR_CODE, "INVALID_INPUT");
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
        errorDetail.setProperty(ERROR_CODE, "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler({ MatchmakingException.class, InsufficientPlayersException.class })
    public ResponseEntity<Object> handleGameRelatedExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = createErrorResponseBody(ex, HttpStatus.BAD_REQUEST, request);
        body.put(ERROR_CODE, "API_ERROR");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ PlayerNotFoundException.class, MeetingPointNotFoundException.class })
    public ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = createErrorResponseBody(ex, HttpStatus.NOT_FOUND, request);
        body.put(ERROR_CODE, "DATA_NOT_FOUND");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationServiceException.class)
    public ResponseEntity<Object> handleLocationServiceException(LocationServiceException ex, WebRequest request) {
        Map<String, Object> body = createErrorResponseBody(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
        body.put(ERROR_CODE, "API_ERROR");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorResponseBody(Exception ex, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));
        return body;
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            AccountStatusException.class,
            AccessDeniedException.class,
            MalformedJwtException.class,
            ExpiredJwtException.class,
            JwtException.class,
            InvalidTokenException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<ProblemDetail> handleSecurityException(Exception exception) {
        ProblemDetail errorDetail;

        if (exception instanceof LockedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "Your account is locked");
            errorDetail.setProperty(ERROR_CODE, "INSUFFICIENT_PERMISSIONS");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "The username or password is incorrect");
            errorDetail.setProperty(ERROR_CODE, "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "The account is locked");
            errorDetail.setProperty(ERROR_CODE, "ACCOUNT_LOCKED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "You are not authorized to access this resource");
            errorDetail.setProperty(ERROR_CODE, "INSUFFICIENT_PERMISSIONS");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        if (exception instanceof MalformedJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty(DESC, "Invalid JWT token format");
            errorDetail.setProperty(ERROR_CODE, "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "The JWT token has expired");
            errorDetail.setProperty(ERROR_CODE, "SESSION_EXPIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof JwtException || exception instanceof InvalidTokenException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "Invalid token");
            errorDetail.setProperty(ERROR_CODE, "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        }

        if (exception instanceof MissingRequestHeaderException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            errorDetail.setProperty(DESC, "Missing Request Header");
            errorDetail.setProperty(ERROR_CODE, "INVALID_INPUT");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
        }

        // Default case
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty(DESC, "Unknown internal server error.");
        errorDetail.setProperty(ERROR_CODE, "UNKNOWN_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = createErrorResponseBody(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
        body.put(ERROR_CODE, "UNKNOWN_ERROR");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}