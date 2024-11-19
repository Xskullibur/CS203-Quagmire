package com.project.G1_T3.admin.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.project.G1_T3.authentication.service.PasswordGeneratorServiceImpl;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.service.UserService;

import java.util.Collections;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordGeneratorServiceImpl passwordGeneratorService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testRegisterAdmin() {
        // Arrange
        String username = "testAdmin";
        String email = "testadmin@example.com";
        String generatedPassword = "generatedPassword123";

        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setUsername(username);
        expectedUserDTO.setEmail(email);
        expectedUserDTO.setUserId(UUID.randomUUID().toString()); // Add an ID to prevent NPE

        when(passwordGeneratorService.generatePassword()).thenReturn(generatedPassword);
        when(userService.registerUser(username, email, generatedPassword, UserRole.ADMIN)).thenReturn(expectedUserDTO);
        when(emailService.sendTempPasswordEmail(any(UserDTO.class), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        UserDTO result = adminService.registerAdmin(username, email);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUserDTO, result);
        verify(passwordGeneratorService).generatePassword();
        verify(userService).registerUser(username, email, generatedPassword, UserRole.ADMIN);
        verify(emailService).sendTempPasswordEmail(any(UserDTO.class), anyString());
    }

    @Test
    void testResetAdminPassword() {
        // Arrange
        UUID adminId = UUID.randomUUID();
        User admin = new User();
        admin.setId(adminId);
        admin.setUsername("testAdmin");
        admin.setEmail("testadmin@example.com");

        String newPassword = "newGeneratedPassword123";

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(passwordGeneratorService.generatePassword()).thenReturn(newPassword);
        when(emailService.sendTempPasswordEmail(any(UserDTO.class), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        adminService.resetAdminPassword(adminId);

        // Assert
        verify(userRepository).findById(adminId);
        verify(passwordGeneratorService).generatePassword();
        verify(userService).updatePassword(admin, newPassword);
        verify(emailService).sendTempPasswordEmail(any(UserDTO.class), eq(newPassword));
    }

    @Test
    void testResetAdminPassword_UserNotFound() {
        // Arrange
        UUID adminId = UUID.randomUUID();
        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> adminService.resetAdminPassword(adminId));
        verify(userRepository).findById(adminId);
        verifyNoInteractions(passwordGeneratorService);
        verifyNoInteractions(emailService);
    }

    @Test
    void testGetPaginatedUsers() {
        // Arrange
        int page = 0;
        int size = 10;
        String field = "username";
        String order = "ASC";

        // Create a user with a valid UUID
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(field).ascending());
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        // Act
        Page<UserDTO> result = adminService.getPaginatedUsers(page, size, field, order);

        // Assert
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        assertNotNull(result.getContent().get(0).getUserId()); // Verify ID is not null
        verify(userRepository).findAll(pageRequest);
    }

    @Test
    void testUpdateUserLockedStatus() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setIsLocked(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        adminService.updateUserLockedStatus(userId, true);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        assertTrue(user.isLocked());
    }

    @Test
    void testUpdateUserLockedStatus_UserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> adminService.updateUserLockedStatus(userId, true));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterAdmin_EmailServiceFailure() {
        // Arrange
        String username = "testAdmin";
        String email = "testadmin@example.com";
        String generatedPassword = "password123";
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(UUID.randomUUID().toString());

        when(passwordGeneratorService.generatePassword()).thenReturn(generatedPassword);
        when(userService.registerUser(anyString(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(userDTO);
        when(emailService.sendTempPasswordEmail(any(UserDTO.class), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Email service failed")));

        // Act & Assert
        // The registration should still succeed even if email fails
        assertDoesNotThrow(() -> adminService.registerAdmin(username, email));
        verify(userService).registerUser(username, email, generatedPassword, UserRole.ADMIN);
    }

    @Test
    void testGetPaginatedUsers_EmptyPage() {
        // Arrange
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username").ascending());
        when(userRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Act
        Page<UserDTO> result = adminService.getPaginatedUsers(page, size, "username", "ASC");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetPaginatedUsers_InvalidPageNumber() {
        // Arrange
        int page = -1;
        int size = 10;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> adminService.getPaginatedUsers(page, size, "username", "ASC"));
    }

    @Test
    void testGetPaginatedUsers_InvalidPageSize() {
        // Arrange
        int page = 0;
        int size = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> adminService.getPaginatedUsers(page, size, "username", "ASC"));
    }
}