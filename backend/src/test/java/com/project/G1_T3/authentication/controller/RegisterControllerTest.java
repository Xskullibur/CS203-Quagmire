package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.model.RegisterRequest;
import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.common.exception.GlobalExceptionHandler;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegisterController registerController;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testRegisterUser_Success() {

        UserDTO userDTO = mock(UserDTO.class);
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserRole role = UserRole.PLAYER;
        when(userService.registerUser(anyString(), anyString(), anyString(), eq(role))).thenReturn(userDTO);

        ResponseEntity<?> response = registerController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());
        verify(userService, times(1)).registerUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            role
        );
    }
    
    @Test
    void testRegisterUser_UsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserRole role = UserRole.PLAYER;
    
        doThrow(new UsernameAlreadyTakenException("Username is already taken"))
            .when(userService).registerUser(anyString(), anyString(), anyString(), eq(role));
    
        try {
            registerController.registerUser(request);
            fail("Expected UsernameAlreadyTakenException was not thrown");
        } catch (UsernameAlreadyTakenException e) {
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleUsernameAlreadyTakenException(e);
            
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ProblemDetail problemDetail = response.getBody();
            assertNotNull(problemDetail);
            assertEquals("Username is already taken", problemDetail.getDetail());
            assertEquals("The username is already taken", problemDetail.getProperties().get("description"));
        }
    }

    @Test
    void testRegisterUser_EmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password");
    
        UserRole role = UserRole.PLAYER;

        doThrow(new EmailAlreadyInUseException("Email is already in use"))
            .when(userService).registerUser(anyString(), anyString(), anyString(), eq(role));
    
        try {
            registerController.registerUser(request);
            fail("Expected EmailAlreadyInUseException was not thrown");
        } catch (EmailAlreadyInUseException e) {
            ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleEmailAlreadyInUseException(e);
            
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ProblemDetail problemDetail = response.getBody();
            assertNotNull(problemDetail);
            assertEquals("Email is already in use", problemDetail.getDetail());
            assertEquals("The email is already in use", problemDetail.getProperties().get("description"));
        }
    }
}