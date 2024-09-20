package com.project.G1_T3.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import com.project.G1_T3.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateAndGenerateToken_ValidCredentials_ReturnsLoginResponseDTO() {
        String username = "testUser";
        String password = "testPassword";
        User user = new User();
        user.setUsername(username);
        user.setId(UUID.randomUUID().toString());
        UserDTO userDTO = UserDTO.fromUser(user);
        String token = "generatedToken";

        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        LoginResponseDTO response = authService.authenticateAndGenerateToken(username, password);

        assertNotNull(response);
        assertEquals(userDTO, response.getUser());
        assertEquals(token, response.getToken());
    }

    @Test
    void authenticateAndGenerateToken_InvalidCredentials_ThrowsBadCredentialsException() {
        String username = "testUser";
        String password = "wrongPassword";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateAndGenerateToken(username, password));
    }

    @Test
    void authenticateAndGenerateToken_UserNotFound_ThrowsBadCredentialsException() {
        String username = "nonExistentUser";
        String password = "testPassword";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateAndGenerateToken(username, password));
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

        UserDTO responseBody = authService.validateToken(token);

        assertEquals(userDTO, responseBody);
    }

    @Test
    void validateToken_UsernameNotFoundException_ThrowsInvalidTokenException() {
        String token = "Bearer invalidToken";
        String jwtToken = "invalidToken";
        String username = "nonExistentUser";

        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
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

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
    }

    @Test
    void validateToken_MalformedToken_ThrowsInvalidTokenException() {
        String token = "MalformedToken";

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
    }

    @Test
    void validateToken_NullToken_ThrowsInvalidTokenException() {
        assertThrows(InvalidTokenException.class, () -> authService.validateToken(null));
    }

}