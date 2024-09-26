package com.project.G1_T3.admin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.G1_T3.authentication.service.PasswordGeneratorService;
import com.project.G1_T3.config.PasswordPolicyConfig;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorServiceTest {

    @Mock
    private PasswordPolicyConfig passwordPolicyConfig;

    @InjectMocks
    private PasswordGeneratorService passwordGeneratorService;

    @Test
    void testGeneratePassword() {
        when(passwordPolicyConfig.getMinLength()).thenReturn(12);
        when(passwordPolicyConfig.requireUppercase()).thenReturn(true);
        when(passwordPolicyConfig.requireLowercase()).thenReturn(true);
        when(passwordPolicyConfig.requireNumbers()).thenReturn(true);
        when(passwordPolicyConfig.requireSpecialChars()).thenReturn(true);

        String password = passwordGeneratorService.generatePassword();

        assertNotNull(password);
        assertEquals(12, password.length());
        assertTrue(password.matches(".*[A-Z].*"));
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*\\d.*"));
        assertTrue(password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"));
    }

    @Test
    void testGeneratePasswordWithDifferentConfig() {
        when(passwordPolicyConfig.getMinLength()).thenReturn(8);
        when(passwordPolicyConfig.requireUppercase()).thenReturn(false);
        when(passwordPolicyConfig.requireLowercase()).thenReturn(true);
        when(passwordPolicyConfig.requireNumbers()).thenReturn(true);
        when(passwordPolicyConfig.requireSpecialChars()).thenReturn(false);

        String password = passwordGeneratorService.generatePassword();

        assertNotNull(password);
        assertEquals(8, password.length());
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*\\d.*"));
        assertFalse(password.matches(".*[A-Z].*"));
        assertFalse(password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"));
    }

    @Test
    void testGeneratePasswordWithZeroLength() {
        when(passwordPolicyConfig.getMinLength()).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> {
            passwordGeneratorService.generatePassword();
        });
    }

    @Test
    void testGeneratePasswordWithNoCharacterTypes() {
        when(passwordPolicyConfig.getMinLength()).thenReturn(12);
        when(passwordPolicyConfig.requireUppercase()).thenReturn(false);
        when(passwordPolicyConfig.requireLowercase()).thenReturn(false);
        when(passwordPolicyConfig.requireNumbers()).thenReturn(false);
        when(passwordPolicyConfig.requireSpecialChars()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            passwordGeneratorService.generatePassword();
        });
    }
}