package com.project.G1_T3.authentication.service;

import java.time.LocalDateTime;

import org.apache.shiro.authc.LockedAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.service.CustomUserDetailsService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserRepository userRepository;

    public LoginResponseDTO authenticateAndGenerateToken(String username, String password) {

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException();
        }

        username = username.toLowerCase();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Checks if user isLocked
            if (user.isLocked()) {
                throw new LockedException("Account is locked.");
            }

            // Authenticate only after checking if locked
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtService.generateToken(user);
            UserDTO userDTO = UserDTO.fromUser(user);

            return new LoginResponseDTO(userDTO, token);

        } catch (AccountStatusException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("The username or password is incorrect", e);
        }
    }

    public UserDTO validateToken(String token) {

        jwtService.validateTokenFormat(token);
        String jwtToken = jwtService.removeTokenPrefix(token);
        String jwtUsername = jwtService.extractUsername(jwtToken);

        CustomUserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class)
                .loadUserByUsername(jwtUsername);

        if (!jwtService.isTokenValid(jwtToken, userDetails)) {
            throw new InvalidTokenException("Invalid Token", jwtToken);
        }

        return UserDTO.fromUser(userDetails.getUser());

    }

    @Override
    public boolean verifyEmail(String token) {

        String username = jwtService.validateEmailVerificationToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("User not found for token", token));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }
}