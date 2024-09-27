package com.project.G1_T3.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
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

    public UserDTO registerUser(String username, String email, String password, UserRole role) {

        username = username.toLowerCase();
        email = email.toLowerCase();

        if (existsByUsername(username)) {
            throw new UsernameAlreadyTakenException("Username is already taken");
        }
        if (existsByEmail(email)) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // Save user to database
        userRepository.save(newUser);
        return UserDTO.fromUser(newUser);
    }

    public boolean existsByUsername(String username) {
        username = username.toLowerCase();
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        email = email.toLowerCase();
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        username = username.toLowerCase();
        return userRepository.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsersWithoutPassword()
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public UserDTO getUserDTOByUsername(String username) {

        final String finalUsername = username.toLowerCase();
        return userRepository.findByUsername(finalUsername)
                .map(UserDTO::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException(finalUsername));
    }
}
