package com.project.G1_T3.security.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.project.G1_T3.common.exception.EmailNotVerifiedException;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;

@ExtendWith(MockitoExtension.class)
class EmailVerificationValidatorTest {

    @InjectMocks
    private EmailVerificationValidator validator;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private User user;

    private static final String TEST_USERNAME = "testuser";

    @Test
    void whenAuthenticationIsNull_thenThrowAccessDeniedException() {
        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> validator.isEmailVerified(null)
        );
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void whenUserNotAuthenticated_thenThrowAccessDeniedException() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> validator.isEmailVerified(authentication)
        );
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void whenPrincipalIsNotCustomUserDetails_thenThrowAccessDeniedException() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("not a CustomUserDetails object");

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> validator.isEmailVerified(authentication)
        );
        assertEquals("Invalid authentication type", exception.getMessage());
    }

    @Test
    void whenEmailNotVerified_thenThrowEmailNotVerifiedException() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(userDetails.getUser()).thenReturn(user);
        when(user.isEmailVerified()).thenReturn(false);

        // Act & Assert
        assertThrows(
            EmailNotVerifiedException.class,
            () -> validator.isEmailVerified(authentication)
        );
        
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(user).isEmailVerified();
        verify(userDetails).getUsername();
    }

    @Test
    void whenEmailVerified_thenReturnTrue() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(user.isEmailVerified()).thenReturn(true);

        // Act
        boolean result = validator.isEmailVerified(authentication);

        // Assert
        assertTrue(result);
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(user).isEmailVerified();
    }

    @Test
    void whenAuthenticatedButNullPrincipal_thenThrowAccessDeniedException() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(null);

        // Act & Assert
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> validator.isEmailVerified(authentication)
        );
        assertEquals("Invalid authentication type", exception.getMessage());
    }

    @Test
    void whenUserDetailsHasNullUser_thenThrowNullPointerException() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(null);

        // Act & Assert
        assertThrows(
            NullPointerException.class,
            () -> validator.isEmailVerified(authentication)
        );
    }
}