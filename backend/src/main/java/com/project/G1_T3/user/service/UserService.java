package com.project.G1_T3.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerProfileService playerProfileService;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Transactional
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

        // Send verification email
        User savedUser = userRepository.save(newUser);

        if (newUser.getRole() == UserRole.PLAYER) {
            sendVerificationEmail(newUser);
        }

        // Create and save a new PlayerProfile
        PlayerProfile newProfile = new PlayerProfile();
        newProfile.setUserId(savedUser.getId());
        // Set other default values for PlayerProfile if needed
        playerProfileService.save(newProfile);

        return UserDTO.fromUser(savedUser);
    }

    public List<User> findUsersByUsernameContaining(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
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
    
    public Optional<User> findByUserId(String userId) {
        return userRepository.findById(UUID.fromString(userId));
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

    public void setUserVerified(User user, boolean isEmailVerified) {

        user.setEmailVerified(isEmailVerified);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void sendVerificationEmail(User user) {
        String token = jwtService.generateEmailVerificationToken(user);
        String verificationLink = backendUrl + "/authentication/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
    }

    public void sendVerificationEmailByUserId(String uuid) {

        Optional<User> userOptional = userRepository.findById(UUID.fromString(uuid));

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(uuid);
        }

        User user = userOptional.get();
        sendVerificationEmail(user);
    }

    public void updatePassword(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
