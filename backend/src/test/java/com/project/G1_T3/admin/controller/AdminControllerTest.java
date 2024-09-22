package com.project.G1_T3.admin.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import com.project.G1_T3.admin.service.AdminService;
import com.project.G1_T3.authentication.model.AdminRegisterRequestDTO;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.UserService;

class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAdminDashboard() {
        ResponseEntity<String> response = adminController.getAdminDashboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome to the admin dashboard!", response.getBody());
    }

    @Test
    void testGetAllUsers() {
        List<UserDTO> expectedUsers = Arrays.asList(
                mock(UserDTO.class),
                mock(UserDTO.class));

        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<UserDTO> actualUsers = adminController.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userService).getAllUsers();
    }

    @Test
    void testRegisterAdmin_Success() {
        AdminRegisterRequestDTO request = new AdminRegisterRequestDTO();
        request.setUsername("newAdmin");
        request.setEmail("newadmin@example.com");

        UserDTO expectedUser = new UserDTO();
        expectedUser.setUsername("newAdmin");
        expectedUser.setEmail("newadmin@example.com");

        when(adminService.registerAdmin("newAdmin", "newadmin@example.com")).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = adminController.postMethodName(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
        verify(adminService).registerAdmin("newAdmin", "newadmin@example.com");
    }

    @Test
    void testRegisterAdmin_Failure() {
        AdminRegisterRequestDTO request = new AdminRegisterRequestDTO();
        request.setUsername("newAdmin");
        request.setEmail("newadmin@example.com");

        when(adminService.registerAdmin("newAdmin", "newadmin@example.com"))
                .thenThrow(new RuntimeException("Registration failed"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminController.postMethodName(request);
        });

        assertEquals("Registration failed", exception.getMessage());
    }

    @Test
    void testUnauthorizedAccess() {
        when(userService.getAllUsers()).thenThrow(new AccessDeniedException("Access denied"));

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            adminController.getAllUsers();
        });

        assertEquals("Access denied", exception.getMessage());
    }
}