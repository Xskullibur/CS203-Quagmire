package com.project.G1_T3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "password.policy")
public class PasswordPolicyConfig {

    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireNumbers;
    private boolean requireSpecialChars;
    private int lockoutAttempts;
    private int lockoutDurationMinutes;

    // Getters and Setters

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean requireUppercase() {
        return requireUppercase;
    }

    public void setRequireUppercase(boolean requireUppercase) {
        this.requireUppercase = requireUppercase;
    }

    public boolean requireLowercase() {
        return requireLowercase;
    }

    public void setRequireLowercase(boolean requireLowercase) {
        this.requireLowercase = requireLowercase;
    }

    public boolean requireNumbers() {
        return requireNumbers;
    }

    public void setRequireNumbers(boolean requireNumbers) {
        this.requireNumbers = requireNumbers;
    }

    public boolean requireSpecialChars() {
        return requireSpecialChars;
    }

    public void setRequireSpecialChars(boolean requireSpecialChars) {
        this.requireSpecialChars = requireSpecialChars;
    }

    public int getLockoutAttempts() {
        return lockoutAttempts;
    }

    public void setLockoutAttempts(int lockoutAttempts) {
        this.lockoutAttempts = lockoutAttempts;
    }

    public int getLockoutDurationMinutes() {
        return lockoutDurationMinutes;
    }

    public void setLockoutDurationMinutes(int lockoutDurationMinutes) {
        this.lockoutDurationMinutes = lockoutDurationMinutes;
    }
}