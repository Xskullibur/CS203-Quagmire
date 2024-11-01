package com.project.G1_T3.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

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
    void registerUser_ValidArguments_ReturnsUserDTO() {

        UserDTO expectedUserDTO = UserDTO.fromUser(testUser);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateEmailVerificationToken(any(User.class))).thenReturn("valid-token");
        when(emailService.sendVerificationEmail(anyString(), anyString(), anyString())).thenReturn(null);
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(null);

        UserDTO actualUserDTO = userService.registerUser(testUser.getUsername(), testUser.getEmail(), testUser.getPasswordHash(), testUser.getRole());

        assertEquals(expectedUserDTO, actualUserDTO);
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateEmailVerificationToken(any(User.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
        verify(playerProfileRepository).save(any(PlayerProfile.class));
    }

    @Test
    void registerUser_UppercaseUsernameAlreadyExistsLowercase_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
            () -> userService.registerUser("ExistingUser", "new@example.com", "password",
                UserRole.PLAYER));

        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    void registerUser_LowercaseUsernameAlreadyExistsUppercase_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
            () -> userService.registerUser("existinguser", "new@example.com", "password",
                UserRole.PLAYER));

        verify(userRepository).existsByUsername("existinguser");
    }

    @Test
    void registerUser_UppercaseEmailAlreadyExistsLowercase_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
            () -> userService.registerUser("newuser", "Existing@Example.com", "password",
                UserRole.PLAYER));

        verify(userRepository).existsByEmail("existing@example.com");
    }

    @Test
    void registerUser_LowercaseEmailAlreadyExistsUppercase_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
            () -> userService.registerUser("newuser", "existing@example.com", "password",
                UserRole.PLAYER));

        verify(userRepository).existsByEmail("existing@example.com");
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsUsernameAlreadyTakenException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class,
            () -> userService.registerUser("existingUser", "new@example.com", "password",
                UserRole.PLAYER));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsEmailAlreadyInUseException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
            () -> userService.registerUser("newUser", "existing@example.com", "password",
                UserRole.PLAYER));
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

        assertThrows(UsernameNotFoundException.class,
            () -> userService.getUserDTOByUsername("nonexistentUser"));
    }
}