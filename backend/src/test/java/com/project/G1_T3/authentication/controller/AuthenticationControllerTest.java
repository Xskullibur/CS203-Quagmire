package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.AuthServiceImpl;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateToken_ValidToken_ReturnsUserDTO() {
        String token = "Bearer valid_token";
        UserDTO userDTO = mock(UserDTO.class);
        userDTO.setUsername("testuser");

        when(authService.validateToken(anyString())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authenticationController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void validateToken_InvalidToken_ThrowsInvalidTokenException() {
        String token = "Bearer invalid_token";

        when(authService.validateToken(anyString())).thenThrow(new InvalidTokenException("Invalid token", token));

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            authenticationController.validateToken(token);
        });

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void validateToken_UsernameNotFoundException_ThrowsInvalidTokenException() {
        String token = "Bearer valid_token";

        when(authService.validateToken(anyString())).thenThrow(new UsernameNotFoundException("User not found"));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationController.validateToken(token);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void validateToken_MalformedToken_ThrowsInvalidTokenException() {
        String token = "Bearer malformed_token";

        when(authService.validateToken(anyString())).thenThrow(new InvalidTokenException("Malformed token", token));

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            authenticationController.validateToken(token);
        });

        assertEquals("Malformed token", exception.getMessage());
    }
}