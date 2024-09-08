package com.project.G1_T3.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String email, String password) {
        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(UserRole.PLAYER); // Set default role
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // Save user to database
        userRepository.save(newUser);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsersWithoutPassword()
            .stream()
            .map(UserDTO::fromUser)
            .collect(Collectors.toList());
    }

    public UserDTO getUserDTOByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
