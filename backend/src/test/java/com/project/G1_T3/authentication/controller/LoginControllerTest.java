package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.authentication.service.AuthService;
import com.project.G1_T3.common.exception.GlobalExceptionHandler;
import com.project.G1_T3.user.model.UserDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private AuthService authService;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        UserDTO userDTO = mock(UserDTO.class);
        LoginResponseDTO responseDTO = new LoginResponseDTO(userDTO, "fake-jwt-token");

        when(authService.authenticateAndGenerateToken(anyString(), anyString())).thenReturn(responseDTO);

        ResponseEntity<?> response = loginController.loginUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authService.authenticateAndGenerateToken(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            loginController.loginUser(request);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLoginUser_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistentuser");
        request.setPassword("password123");

        when(authService.authenticateAndGenerateToken(anyString(), anyString()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            loginController.loginUser(request);
        });

        assertEquals("User not found", exception.getMessage());
    }

}