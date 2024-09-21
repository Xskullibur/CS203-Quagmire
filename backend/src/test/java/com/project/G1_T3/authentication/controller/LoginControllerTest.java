package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.authentication.service.AuthService;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.GlobalExceptionHandler;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoginControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginController loginController;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testLoginUser_ValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correctpassword");

        User mockUser = new User();
        mockUser.setId("cccccccc-cccc-cccc-cccc-cccccccccccc");
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash(passwordEncoder.encode("correctpassword"));

        when(authService.authenticateUser(anyString(), anyString())).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = loginController.loginUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponseDTO);
        LoginResponseDTO responseDTO = (LoginResponseDTO) response.getBody();
        assertNotNull(responseDTO);
        assertEquals("mocked-jwt-token", responseDTO.getToken());
        assertNotNull(responseDTO.getUser());
        assertEquals("testuser", responseDTO.getUser().getUsername());
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authService.authenticateUser(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleSecurityException(
                new BadCredentialsException("Invalid credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("Invalid credentials", problemDetail.getDetail());
        assertEquals("The username or password is incorrect", problemDetail.getProperties().get("description"));
    }

    @Test
    void testLoginUser_IncorrectPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setId("cccccccc-cccc-cccc-cccc-cccccccccccc");
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setRole(UserRole.PLAYER);
        user.setPasswordHash("encodedPassword");

        when(authService.authenticateUser(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        try {
            loginController.loginUser(request);
            fail("Expected BadCredentialsException was not thrown");
        } catch (BadCredentialsException e) {
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleSecurityException(e);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            ProblemDetail problemDetail = response.getBody();
            assertNotNull(problemDetail);
            assertEquals("Invalid username or password", problemDetail.getDetail());
            assertEquals("The username or password is incorrect", problemDetail.getProperties().get("description"));
        }
    }
}