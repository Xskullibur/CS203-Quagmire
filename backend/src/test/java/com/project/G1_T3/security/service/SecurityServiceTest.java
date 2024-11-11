package com.project.G1_T3.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import java.util.UUID;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SecurityService securityService;

    private CustomUserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        // Create user with a fixed UUID
        user = new User();
        user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        userDetails = new CustomUserDetails(user);

        // Set up SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAuthenticatedUser_WhenUserIsAuthenticated_ShouldReturnUserDetails() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        CustomUserDetails result = securityService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals(userDetails, result);
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    void getAuthenticatedUser_WhenNoAuthentication_ShouldReturnNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        CustomUserDetails result = securityService.getAuthenticatedUser();

        // Assert
        assertNull(result);
    }

    @Test
    void getAuthenticatedUser_WhenPrincipalNotCustomUserDetails_ShouldReturnNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not a CustomUserDetails object");

        // Act
        CustomUserDetails result = securityService.getAuthenticatedUser();

        // Assert
        assertNull(result);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
