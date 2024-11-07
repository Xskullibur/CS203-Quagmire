package com.project.G1_T3.authentication.model;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
