package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.AuthenticationFailedException;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.common.exception.ValidationException;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import com.project.G1_T3.user.service.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    public LoginResponseDTO authenticateAndGenerateToken(String username, String password) {

        // Validate arguments
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new ValidationException("Username and password are required");
        }

        username = username.toLowerCase().trim();

        try {
            // Attempt authentication
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // If we get here, authentication was successful
            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

            // Check account status
            if (user.isLocked()) {
                throw new LockedException("Account is locked");
            }

            // Generate token and create response
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateToken(user);
            UserDTO userDTO = UserDTO.fromUser(user);

            return new LoginResponseDTO(userDTO, token);

        } catch (LockedException e) {
            throw new LockedException("Account is locked");
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new BadCredentialsException("Authentication failed");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Authentication failed");
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
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("User not found for token", token));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        userService.setUserVerified(user, true);

        return true;
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the current {@link User} object associated with the authenticated
     *         session.
     * @throws IllegalStateException if the authentication or principal is null.
     * @throws ClassCastException    if the principal is not an instance of
     *                               {@link CustomUserDetails}.
     */
    @Override
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ClassCastException("Principal is not an instance of CustomUserDetails");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}