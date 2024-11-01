package com.project.G1_T3.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.common.exception.ValidationException;
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
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void authenticateAndGenerateToken_ValidCredentials_ReturnsLoginResponseDTO() {
        // Arrange
        String username = "testuser";
        String password = "testPassword";
        User user = new User();
        user.setUsername(username);
        user.setUserId(UUID.randomUUID());
        user.setIsLocked(false);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        UserDTO userDTO = UserDTO.fromUser(user);
        String token = "generatedToken";

        // Mock authentication success
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(user)).thenReturn(token);
        doNothing().when(securityContext).setAuthentication(authentication);

        // Act
        LoginResponseDTO response = authService.authenticateAndGenerateToken(username, password);

        // Assert
        assertNotNull(response);
        assertEquals(userDTO, response.getUser());
        assertEquals(token, response.getToken());

        // Verify interactions
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(authentication);
        verify(jwtService).generateToken(user);
    }

    @Test
    void authenticateAndGenerateToken_InvalidCredentials_ThrowsBadCredentialsException() {
        // Arrange
        String username = "testUser";
        String password = "wrongPassword";

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class,
                () -> authService.authenticateAndGenerateToken(username, password));
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext, never()).setAuthentication(any());
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

    @Test
    void authenticateAndGenerateToken_NullUsername_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> authService.authenticateAndGenerateToken(null, "password"));
    }

    @Test
    void authenticateAndGenerateToken_BlankUsername_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> authService.authenticateAndGenerateToken("  ", "password"));
    }

    @Test
    void authenticateAndGenerateToken_NullPassword_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> authService.authenticateAndGenerateToken("username", null));
    }

    @Test
    void authenticateAndGenerateToken_BlankPassword_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> authService.authenticateAndGenerateToken("username", "  "));
    }

    @Test
    void authenticateAndGenerateToken_LockedAccount_ThrowsLockedException() {
        String username = "lockedUser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setIsLocked(true);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));

        assertThrows(LockedException.class,
                () -> authService.authenticateAndGenerateToken(username, password));
    }

    @Test
    void verifyEmail_ValidToken_ReturnsTrue() {
        String token = "validToken";
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setEmailVerified(false);

        when(jwtService.validateEmailVerificationToken(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        doNothing().when(userService).setUserVerified(user, true);

        boolean result = authService.verifyEmail(token);

        assertTrue(result);
        verify(userService).setUserVerified(user, true);
    }

    @Test
    void verifyEmail_AlreadyVerified_ThrowsIllegalStateException() {
        String token = "validToken";
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setEmailVerified(true);

        when(jwtService.validateEmailVerificationToken(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> authService.verifyEmail(token));
    }

    @Test
    void verifyEmail_UserNotFound_ThrowsInvalidTokenException() {
        String token = "validToken";
        String username = "testUser";

        when(jwtService.validateEmailVerificationToken(token)).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.verifyEmail(token));
    }

    @Test
    void getCurrentUser_ValidAuthentication_ReturnsUser() {
        User expectedUser = new User();
        CustomUserDetails userDetails = new CustomUserDetails(expectedUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        User result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals(expectedUser, result);
    }

    @Test
    void getCurrentUser_NoAuthentication_ThrowsIllegalStateException() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> authService.getCurrentUser());
    }

    @Test
    void getCurrentUser_NoPrincipal_ThrowsIllegalStateException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> authService.getCurrentUser());
    }

    @Test
    void getCurrentUser_InvalidPrincipalType_ThrowsClassCastException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("invalid-principal-type");

        assertThrows(ClassCastException.class, () -> authService.getCurrentUser());
    }

}