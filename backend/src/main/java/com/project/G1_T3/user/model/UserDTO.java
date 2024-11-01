package com.project.G1_T3.user.model;

import com.project.G1_T3.player.model.PlayerProfile;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private String userId;
    private String username;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean emailVerified;
    private PlayerProfile playerProfile;
    private boolean isLocked;

    public UserDTO(boolean emailVerified) {
        super();
        this.emailVerified = emailVerified;
    }

    public UserDTO(UUID userId, String username, String email, UserRole role,
        LocalDateTime createdAt, LocalDateTime updatedAt, boolean emailVerified, boolean isLocked) {
        this.userId = userId.toString();
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.emailVerified = emailVerified;
        this.isLocked = isLocked;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.isEmailVerified(),
            user.isLocked()
        );
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
        return Objects.equals(userId, other.userId) && Objects.equals(username, other.username)
            && Objects.equals(email, other.email) && role == other.role && Objects.equals(createdAt,
            other.createdAt) && Objects.equals(updatedAt, other.updatedAt);
    }

}