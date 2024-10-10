package com.project.G1_T3.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.CustomUserDetails;
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
    private AuthServiceImpl authService;

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

    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateAndGenerateToken_ValidCredentials_ReturnsLoginResponseDTO() {
        String username = "testuser";
        String password = "testPassword";
        User user = new User();
        user.setUsername(username);
        user.setUserId(UUID.randomUUID());
        UserDTO userDTO = UserDTO.fromUser(user);
        String token = "generatedToken";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername(username.toLowerCase())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        LoginResponseDTO response = authService.authenticateAndGenerateToken(username, password);

        assertNotNull(response);
        assertEquals(userDTO, response.getUser());
        assertEquals(token, response.getToken());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(username.toLowerCase());
    }

    @Test
    void authenticateAndGenerateToken_InvalidCredentials_ThrowsBadCredentialsException() {
        String username = "testUser";
        String password = "wrongPassword";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateAndGenerateToken(username, password));
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateAndGenerateToken_UserNotFound_ThrowsBadCredentialsException() {
        String username = "nonExistentUser";
        String password = "testPassword";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername(username.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.authenticateAndGenerateToken(username, password));
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(username.toLowerCase());
    }

    @Test
    void validateToken_ValidToken_ReturnsUserDTO() {
        String token = "Bearer validToken";
        String jwtToken = "validToken";
        String username = "testUser";

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUsername(username);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        doNothing().when(jwtService).validateTokenFormat(token);
        when(jwtService.removeTokenPrefix(token)).thenReturn(jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(applicationContext.getBean(CustomUserDetailsService.class)).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);

        UserDTO resultUserDTO = authService.validateToken(token);

        assertNotNull(resultUserDTO);
        assertEquals(username, resultUserDTO.getUsername());
        verify(jwtService).validateTokenFormat(token);
        verify(jwtService).removeTokenPrefix(token);
        verify(jwtService).extractUsername(jwtToken);
        verify(jwtService).isTokenValid(jwtToken, userDetails);
    }

    @Test
    void validateToken_InvalidToken_ThrowsInvalidTokenException() {
        String token = "Bearer invalidToken";
        String jwtToken = "invalidToken";
        String username = "testUser";
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        doNothing().when(jwtService).validateTokenFormat(token);
        when(jwtService.removeTokenPrefix(token)).thenReturn(jwtToken);
        when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        when(applicationContext.getBean(CustomUserDetailsService.class)).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
        verify(jwtService).validateTokenFormat(token);
        verify(jwtService).removeTokenPrefix(token);
        verify(jwtService).extractUsername(jwtToken);
        verify(jwtService).isTokenValid(jwtToken, userDetails);
    }

    @Test
    void validateToken_MalformedToken_ThrowsInvalidTokenException() {
        String token = "MalformedToken";

        doThrow(new InvalidTokenException("Invalid token format", token)).when(jwtService).validateTokenFormat(token);

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
        verify(jwtService).validateTokenFormat(token);
    }

}