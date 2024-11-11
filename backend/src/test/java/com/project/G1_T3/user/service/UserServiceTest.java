package com.project.G1_T3.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.G1_T3.authentication.model.ResetPasswordDTO;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.UpdateEmailDTO;
import com.project.G1_T3.user.model.UpdatePasswordDTO;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
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
    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(UserRole.PLAYER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    class RegistrationTests {
        @Test
        void registerUser_ValidPlayerArguments_ReturnsUserDTO() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtService.generateEmailVerificationToken(any())).thenReturn("valid-token");

            UserDTO result = userService.registerUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, UserRole.PLAYER);

            assertNotNull(result);
            assertEquals(TEST_USERNAME, result.getUsername());
            verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
        }

        @Test
        void registerUser_ValidAdminArguments_ReturnsUserDTOWithoutVerification() {
            testUser.setRole(UserRole.ADMIN);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserDTO result = userService.registerUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, UserRole.ADMIN);

            assertNotNull(result);
            assertEquals(TEST_USERNAME, result.getUsername());
            verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString());
            verify(playerProfileRepository, never()).save(any(PlayerProfile.class));
        }

        @ParameterizedTest
        @ValueSource(strings = { "TestUser", "testUser", "TESTUSER" })
        void registerUser_UsernameAlreadyExists_ThrowsUsernameAlreadyTakenException(String username) {
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            assertThrows(UsernameAlreadyTakenException.class,
                    () -> userService.registerUser(username, TEST_EMAIL, TEST_PASSWORD, UserRole.PLAYER));

            verify(userRepository).existsByUsername(username.toLowerCase());
        }

        @ParameterizedTest
        @ValueSource(strings = { "Test@Example.com", "test@example.com", "TEST@EXAMPLE.COM" })
        void registerUser_EmailAlreadyExists_ThrowsEmailAlreadyInUseException(String email) {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            assertThrows(EmailAlreadyInUseException.class,
                    () -> userService.registerUser(TEST_USERNAME, email, TEST_PASSWORD, UserRole.PLAYER));

            verify(userRepository).existsByEmail(email.toLowerCase());
        }
    }

    @Nested
    class UserSearchTests {
        @Test
        void findUsersByUsernameContaining_ValidPattern_ReturnsMatchingUsers() {
            User user1 = createTestUser("test1");
            User user2 = createTestUser("test2");
            when(userRepository.findByUsernameContainingIgnoreCase("test"))
                    .thenReturn(Arrays.asList(user1, user2));

            var results = userService.findUsersByUsernameContaining("test");

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(u -> u.getUsername().contains("test")));
        }

        @Test
        void findByUserId_ValidId_ReturnsUser() {
            UUID userId = UUID.randomUUID();
            testUser.setId(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            Optional<User> result = userService.findByUserId(userId.toString());

            assertTrue(result.isPresent());
            assertEquals(userId.toString(), result.get().getId().toString());
        }

        @Test
        void findByUserId_InvalidId_ReturnsEmpty() {
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Optional<User> result = userService.findByUserId(UUID.randomUUID().toString());

            assertFalse(result.isPresent());
        }

        @Test
        void getUserDTOByUsername_UsernameExists_ReturnsUserDTO() {
            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

            UserDTO result = userService.getUserDTOByUsername(TEST_USERNAME);

            assertNotNull(result);
            assertEquals(TEST_USERNAME, result.getUsername());
        }

        @Test
        void getUserDTOByUsername_UsernameDoesNotExist_ThrowsUsernameNotFoundException() {
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> userService.getUserDTOByUsername(TEST_USERNAME));
        }
    }

    @Nested
    class UserManagementTests {
        @Test
        void setUserVerified_ValidUser_UpdatesVerificationStatus() {
            LocalDateTime originalUpdateTime = testUser.getUpdatedAt();
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.setUserVerified(testUser, true);

            assertTrue(testUser.isEmailVerified());
            assertTrue(testUser.getUpdatedAt().isEqual(originalUpdateTime)
                    || testUser.getUpdatedAt().isAfter(originalUpdateTime));
            verify(userRepository).save(testUser);
        }

        @Test
        void sendVerificationEmailByUserId_ValidId_SendsEmail() {
            UUID userId = UUID.randomUUID();
            testUser.setId(userId);
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
            when(jwtService.generateEmailVerificationToken(any())).thenReturn("valid-token");

            userService.sendVerificationEmailByUserId(userId.toString());

            verify(jwtService).generateEmailVerificationToken(testUser);
            verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
        }

        @Test
        void sendVerificationEmailByUserId_InvalidId_ThrowsUsernameNotFoundException() {
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> userService.sendVerificationEmailByUserId(UUID.randomUUID().toString()));
        }

        @Test
        void updatePassword_ValidPassword_UpdatesUserPassword() {
            String newPassword = "newPassword";
            String encodedPassword = "encodedPassword";
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updatePassword(testUser, newPassword);

            assertEquals(encodedPassword, testUser.getPasswordHash());
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).save(testUser);
        }
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(UserRole.PLAYER);
        return user;
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("testuser");
        resetPasswordDTO.setCurrentPassword("currentPassword");
        resetPasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "hashedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        // Act
        userService.resetPassword(resetPasswordDTO);

        // Assert
        assertTrue(testUser.getPasswordHash().equals("newHashedPassword"));
    }

    @Test
    void resetPassword_IncorrectCurrentPassword() {
        // Arrange
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("testuser");
        resetPasswordDTO.setCurrentPassword("wrongPassword");
        resetPasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedpassword")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.resetPassword(resetPasswordDTO));
    }

    @Test
    void resetPassword_UserNotFound() {
        // Arrange
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("nonexistentuser");
        resetPasswordDTO.setCurrentPassword("currentPassword");
        resetPasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.resetPassword(resetPasswordDTO));
    }

    @Test
    void updatePassword_Success() {
        // Arrange
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setCurrentPassword("currentPassword");
        updatePasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "hashedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        // Act
        userService.updatePassword("testuser", updatePasswordDTO);

        // Assert
        assertTrue(testUser.getPasswordHash().equals("newHashedPassword"));
    }

    @Test
    void updatePassword_IncorrectCurrentPassword() {
        // Arrange
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setCurrentPassword("wrongPassword");
        updatePasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedpassword")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword("testuser", updatePasswordDTO));
    }

    @Test
    void updatePassword_UserNotFound() {
        // Arrange
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setCurrentPassword("currentPassword");
        updatePasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.updatePassword("nonexistentuser", updatePasswordDTO));
    }

    @Test
    void updateEmail_Success() {
        // Arrange
        UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
        updateEmailDTO.setNewEmail("new@example.com");
        updateEmailDTO.setPassword("currentPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "hashedpassword")).thenReturn(true);

        // Act
        boolean result = userService.updateEmail("testuser", updateEmailDTO);

        // Assert
        assertTrue(result);
        assertTrue(testUser.getEmail().equals("new@example.com"));
    }

    @Test
    void updateEmail_IncorrectPassword() {
        // Arrange
        UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
        updateEmailDTO.setNewEmail("new@example.com");
        updateEmailDTO.setPassword("wrongPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedpassword")).thenReturn(false);

        // Act
        boolean result = userService.updateEmail("testuser", updateEmailDTO);

        // Assert
        assertTrue(!result); // Ensure that the email was not updated
    }

    @Test
    void updateEmail_UserNotFound() {
        // Arrange
        UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
        updateEmailDTO.setNewEmail("new@example.com");
        updateEmailDTO.setPassword("currentPassword");

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.updateEmail("nonexistentuser", updateEmailDTO));
    }
}
