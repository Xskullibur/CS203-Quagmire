package com.project.G1_T3.authentication.model;

public class LoginResponse {
    private String userId;
    private String username;
    private String token;

    public LoginResponse(String userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}