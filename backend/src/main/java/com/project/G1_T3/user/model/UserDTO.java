package com.project.G1_T3.user.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO() {
        super();
    }

    public UserDTO(UUID userId, String username, String email, UserRole role, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.userId = userId.toString();
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email, role, createdAt, updatedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDTO other = (UserDTO) obj;
        return Objects.equals(userId, other.userId) &&
                Objects.equals(username, other.username) &&
                Objects.equals(email, other.email) &&
                role == other.role &&
                Objects.equals(createdAt, other.createdAt) &&
                Objects.equals(updatedAt, other.updatedAt);
    }

}