package com.project.G1_T3.authentication.model;

import java.util.UUID;

import com.project.G1_T3.user.model.UserRole;

public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private UserRole role;

    public UserDTO(UUID userId, String username, String email, UserRole role) {
        this.userId = userId.toString();
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}