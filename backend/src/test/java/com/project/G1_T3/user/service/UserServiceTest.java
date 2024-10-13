package com.project.G1_T3.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(UserRole.PLAYER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerUser_UppercaseUsernameAlreadyExistsLowercase_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.registerUser("ExistingUser", "new@example.com", "password", UserRole.PLAYER));

        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    void registerUser_LowercaseUsernameAlreadyExistsUppercase_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.registerUser("existinguser", "new@example.com", "password", UserRole.PLAYER));

        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    void registerUser_UppercaseEmailAlreadyExistsLowercase_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
                () -> userService.registerUser("newuser", "Existing@Example.com", "password", UserRole.PLAYER));

        verify(userRepository).existsByEmail("existing@example.com");
    }

    @Test
    void registerUser_LowercaseEmailAlreadyExistsUppercase_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
                () -> userService.registerUser("newuser", "existing@example.com", "password", UserRole.PLAYER));

        verify(userRepository).existsByEmail("existing@example.com");
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
                () -> userService.registerUser("existingUser", "new@example.com", "password", UserRole.PLAYER));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
                () -> userService.registerUser("newUser", "existing@example.com", "password", UserRole.PLAYER));
    }

    @Test
    void existsByUsername_UsernameExists_ReturnsTrue() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertTrue(userService.existsByUsername("existingUser"));
    }

    @Test
    void existsByUsername_UsernameDoesNotExist_ReturnsFalse() {
        when(userRepository.existsByUsername("nonexistentuser")).thenReturn(false);

        assertFalse(userService.existsByUsername("nonexistentUser"));
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("existing@example.com"));
    }

    @Test
    void existsByEmail_EmailDoesNotExist_ReturnsFalse() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertFalse(userService.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void findByUsername_UsernameExists_ReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_UsernameDoesNotExist_ReturnsEmpty() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistentUser");

        assertFalse(result.isPresent());
    }

    @Test
    void getAllUsers_UsersExist_ReturnsUserList() {
        User user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("user2");

        when(userRepository.findAllUsersWithoutPassword()).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void getUserDTOByUsername_UsernameExists_ReturnsUserDTO() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserDTOByUsername("testUser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserDTOByUsername_UsernameDoesNotExist_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserDTOByUsername("nonexistentUser"));
    }
}