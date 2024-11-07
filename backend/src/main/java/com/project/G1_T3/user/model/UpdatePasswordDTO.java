package com.project.G1_T3.user.model;

import jakarta.validation.constraints.NotBlank;

public class UpdatePasswordDTO {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;

    // Getters and setters
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
