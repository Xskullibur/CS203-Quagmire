package com.project.G1_T3.user.repository;

import com.project.G1_T3.user.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long deleteByUsername(String username);

    void deleteByEmail(String email);

    List<User> findByUsernameContainingIgnoreCase(String username);

    @Query("SELECT new com.project.G1_T3.user.model.UserDTO(u.userId, u.username, u.email, u.role, u.createdAt, u.updatedAt, u.emailVerified, u.isLocked) FROM User u")
    List<User> findAllUsersWithoutPassword();
}