package com.project.G1_T3.security.service;

import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AuthorizationService authorizationService;

    private UUID userId;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Create a fixed UUID for testing
        userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Initialize User with the UUID
        User user = new User();
        user.setId(userId);

        // Create CustomUserDetails with the User
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void authorizeUserById_WhenUserAuthorized_ShouldNotThrowException() {
        // Arrange
        // Mock the SecurityService to return our userDetails
        when(securityService.getAuthenticatedUser()).thenReturn(userDetails);

        // Use the same UUID that we set in the user object
        UUID requestedUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act & Assert
        assertDoesNotThrow(() -> authorizationService.authorizeUserById(requestedUserId));

        // Verify that getAuthenticatedUser was called
        verify(securityService).getAuthenticatedUser();
    }

    @Test
    void authorizeUserById_WhenUserNotAuthorized_ShouldThrowSecurityException() {
        // Arrange
        when(securityService.getAuthenticatedUser()).thenReturn(userDetails);

        // Use a different UUID
        UUID differentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> authorizationService.authorizeUserById(differentUserId));
        assertEquals("User not authorized to update this profile", exception.getMessage());

        // Verify that getAuthenticatedUser was called
        verify(securityService).getAuthenticatedUser();
    }

    @Test
    void authorizeUserById_WhenNoAuthenticatedUser_ShouldThrowSecurityException() {
        // Arrange
        when(securityService.getAuthenticatedUser()).thenReturn(null);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> authorizationService.authorizeUserById(userId));
        assertEquals("User not authorized to update this profile", exception.getMessage());

        // Verify that getAuthenticatedUser was called
        verify(securityService).getAuthenticatedUser();
    }
}
