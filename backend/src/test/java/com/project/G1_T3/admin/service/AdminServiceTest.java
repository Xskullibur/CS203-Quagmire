package com.project.G1_T3.admin.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project.G1_T3.authentication.service.PasswordGeneratorServiceImpl;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordGeneratorServiceImpl passwordGeneratorService;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testRegisterAdmin() {

        String username = "testAdmin";
        String email = "testadmin@example.com";
        String generatedPassword = "generatedPassword123";

        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setUsername(username);
        expectedUserDTO.setEmail(email);

        when(passwordGeneratorService.generatePassword()).thenReturn(generatedPassword);
        when(userService.registerUser(username, email, generatedPassword, UserRole.ADMIN)).thenReturn(expectedUserDTO);

        UserDTO result = adminService.registerAdmin(username, email);

        assertNotNull(result);
        assertEquals(expectedUserDTO, result);
        verify(passwordGeneratorService).generatePassword();
        verify(userService).registerUser(username, email, generatedPassword, UserRole.ADMIN);
    }
}