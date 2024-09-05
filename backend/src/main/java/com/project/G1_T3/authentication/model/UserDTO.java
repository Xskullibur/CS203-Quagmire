package com.project.G1_T3.authentication.model;

import java.util.UUID;

public class UserDTO {
    private String userId;
    private String username;
    private String email;

    public UserDTO(UUID userId, String username, String email) {
        this.userId = userId.toString();
        this.username = username;
        this.email = email;
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
}