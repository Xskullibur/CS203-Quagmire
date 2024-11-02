package com.project.G1_T3.user.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.model.UpdatePasswordDTO;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.EmailAlreadyInUseException;
import com.project.G1_T3.common.exception.RegistrationException;
import com.project.G1_T3.common.exception.UsernameAlreadyTakenException;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerProfileRepository playerProfileRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Transactional
    public UserDTO registerUser(String username, String email, String password, UserRole role) {

        try {

            username = username.toLowerCase();
            email = email.toLowerCase();

            if (existsByUsername(username)) {
                throw new UsernameAlreadyTakenException("Username is already taken");
            }
            if (existsByEmail(email)) {
                throw new EmailAlreadyInUseException("Email is already in use");
            }

            // Create new user
            User newUser = createUser(username, email, password, role);
            User savedUser = userRepository.save(newUser);

            // Send verification email
            if (newUser.getRole() == UserRole.PLAYER) {

                try {
                    sendVerificationEmail(newUser);
                    createPlayerProfile(savedUser);
                } catch (Exception e) {
                    logger.error("Error in post-registration process", e);
                }

            }

            return UserDTO.fromUser(savedUser);

        } catch (UsernameAlreadyTakenException | EmailAlreadyInUseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Registration failed", e);
            throw new RegistrationException("Failed to complete registration");
        }
    }

    private void createPlayerProfile(User user) {
        PlayerProfile newProfile = new PlayerProfile();
        newProfile.setUser(user);
        playerProfileRepository.save(newProfile);
    }

    private User createUser(String username, String email, String password, UserRole role) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        return newUser;
    }

    public boolean existsByUsername(String username) {
        username = username.toLowerCase();
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        email = email.toLowerCase();
        return userRepository.existsByEmail(email);
    }

    public void sendVerificationEmail(User user) {
        String token = jwtService.generateEmailVerificationToken(user);
        String verificationLink = backendUrl + "/authentication/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
    }

    public List<User> findUsersByUsernameContaining(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public Optional<User> findByUsername(String username) {
        username = username.toLowerCase();
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserId(String userId) {
        return userRepository.findById(UUID.fromString(userId));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsersWithoutPassword().stream().map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public UserDTO getUserDTOByUsername(String username) {

        final String finalUsername = username.toLowerCase();
        return userRepository.findByUsername(finalUsername).map(UserDTO::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException(finalUsername));
    }

    public void setUserVerified(User user, boolean isEmailVerified) {

        user.setEmailVerified(isEmailVerified);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
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

    // public void updatePassword(UUID userId, String oldPassword, String newPassword) {
    //     // Find user by ID
    //     User user = userRepository.findById(userId)
    //             .orElseThrow(() -> new RuntimeException("User not found"));

    //     // Check if old password matches
    //     if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
    //         throw new RuntimeException("Old password is incorrect");
    //     }

    //     // Update the password and save
    //     user.setPasswordHash(passwordEncoder.encode(newPassword));
    //     userRepository.save(user);
    // }

    public void resetPassword(UpdatePasswordDTO updatePasswordDTO) {
        User user = userRepository.findByUsername(updatePasswordDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(updatePasswordDTO.getUsername()));

        String currentPassword = updatePasswordDTO.getCurrentPassword();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    public UserDTO getUserInfo(String username) {
        // Find the user by username in the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Convert the User entity to a UserDTO
        return UserDTO.fromUser(user);
    }
}
