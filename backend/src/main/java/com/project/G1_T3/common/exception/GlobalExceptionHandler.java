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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DESC = "description";

    // UsernameAlreadyTakenException handler
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ProblemDetail> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The username is already taken");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    // EmailAlreadyInUseException handler
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUseException(EmailAlreadyInUseException e) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        errorDetail.setProperty(DESC, "The email is already in use");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    // MethodArgumentNotValidException handler
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

    // Specific exception handlers with HttpStatus and custom message
    @ExceptionHandler({ InvalidTokenException.class, BadCredentialsException.class, AccountStatusException.class,
            AccessDeniedException.class, ExpiredJwtException.class })
    public ResponseEntity<ProblemDetail> handleSecurityAndAuthExceptions(Exception exception) {

        ProblemDetail errorDetail = null;
        exception.printStackTrace(); // Log for debugging

        if (exception instanceof InvalidTokenException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        } else if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty(DESC, "The username or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
        } else if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "The account is locked");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        } else if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "You are not authorized to access this resource");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        } else if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty(DESC, "The JWT token has expired");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetail);
        }

        // Fallback for unexpected exceptions
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.");
        errorDetail.setProperty(DESC, "Unknown internal server error.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
    }

    // Custom matchmaking and game-related exception handlers
    @ExceptionHandler({ MatchmakingException.class, InsufficientPlayersException.class })
    public ResponseEntity<Object> handleGameRelatedExceptions(Exception ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ PlayerNotFoundException.class, MeetingPointNotFoundException.class })
    public ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(LocationServiceException.class)
    public ResponseEntity<Object> handleLocationServiceException(LocationServiceException ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> createErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, status);
    }
}
