package com.project.G1_T3.authentication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import com.project.G1_T3.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateToken_ValidToken_ReturnsUserDTO() {
        String token = "Bearer validToken";
        String jwtToken = "validToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);
        UserDTO userDTO = mock(UserDTO.class);
        userDTO.setUsername(username);

        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doNothing().when(jwtService).validateToken(jwtToken, userDetails);
        when(userService.getUserDTOByUsername(username)).thenReturn(userDTO);

        ResponseEntity<?> response = authenticationController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void validateToken_UsernameNotFoundException_ThrowsInvalidTokenException() {
        String token = "Bearer invalidToken";
        String jwtToken = "invalidToken";
        String username = "nonExistentUser";

        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(InvalidTokenException.class, () -> authenticationController.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ThrowsInvalidTokenException() {
        String token = "Bearer invalidToken";
        String jwtToken = "invalidToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doThrow(new InvalidTokenException("Invalid token", jwtToken)).when(jwtService).validateToken(jwtToken,
                userDetails);

        assertThrows(InvalidTokenException.class, () -> authenticationController.validateToken(token));
    }
}