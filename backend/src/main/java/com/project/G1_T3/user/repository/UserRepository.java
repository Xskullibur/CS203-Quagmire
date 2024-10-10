package com.project.G1_T3.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.G1_T3.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long deleteByUsername(String username);

    long deleteByEmail(String email);

    @Query("SELECT new User(u.userId, u.username, u.email, null, u.role, u.createdAt, u.updatedAt) FROM User u")
    List<User> findAllUsersWithoutPassword();
}