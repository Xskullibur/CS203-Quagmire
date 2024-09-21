package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.G1_T3.config.PasswordPolicyConfig;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PasswordGeneratorService {

    @Autowired
    private PasswordPolicyConfig passwordPolicyConfig;

    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        int length = passwordPolicyConfig.getMinLength(); // or any logic to determine length

        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than zero");
        }

        String validChars = buildValidCharacters();

        if (validChars.isEmpty()) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(validChars.charAt(random.nextInt(validChars.length())));
        }

        return ensurePasswordStrength(password.toString());
    }

    private String buildValidCharacters() {
        return Stream.of(
                passwordPolicyConfig.requireUppercase() ? UPPERCASE_CHARS : "",
                passwordPolicyConfig.requireLowercase() ? LOWERCASE_CHARS : "",
                passwordPolicyConfig.requireNumbers() ? NUMBER_CHARS : "",
                passwordPolicyConfig.requireSpecialChars() ? SPECIAL_CHARS : "")
                .collect(Collectors.joining());
    }

    private String ensurePasswordStrength(String password) {
        List<Character> missingChars = new ArrayList<>();

        if (passwordPolicyConfig.requireUppercase() && !password.chars().anyMatch(Character::isUpperCase)) {
            missingChars.add(randomCharFrom(UPPERCASE_CHARS));
        }
        if (passwordPolicyConfig.requireLowercase() && !password.chars().anyMatch(Character::isLowerCase)) {
            missingChars.add(randomCharFrom(LOWERCASE_CHARS));
        }
        if (passwordPolicyConfig.requireNumbers() && !password.chars().anyMatch(Character::isDigit)) {
            missingChars.add(randomCharFrom(NUMBER_CHARS));
        }
        if (passwordPolicyConfig.requireSpecialChars()
                && !password.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0)) {
            missingChars.add(randomCharFrom(SPECIAL_CHARS));
        }

        if (!missingChars.isEmpty()) {
            StringBuilder strengthenedPassword = new StringBuilder(password);
            for (char c : missingChars) {
                int randomIndex = random.nextInt(strengthenedPassword.length());
                strengthenedPassword.setCharAt(randomIndex, c);
            }
            return strengthenedPassword.toString();
        }

        return password;
    }

    private char randomCharFrom(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}