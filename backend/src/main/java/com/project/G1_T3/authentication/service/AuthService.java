package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import com.project.G1_T3.user.service.UserService;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    public LoginResponseDTO authenticateAndGenerateToken(String username, String password) {

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtService.generateToken(user);
            UserDTO userDTO = UserDTO.fromUser(user);

            return new LoginResponseDTO(userDTO, token);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("The username or password is incorrect", e);
        }
    }

    public UserDTO validateToken(String token) {

        if (token == null) {
            throw new InvalidTokenException("Token cannot be null", null);
        }

        if (!token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format", token);
        }

        try {
            String jwtToken = token.substring(7);
            String jwtUsername = jwtService.extractUsername(jwtToken);

            UserDetails user = userDetailsService.loadUserByUsername(jwtUsername);
            jwtService.validateToken(jwtToken, user);

            return userService.getUserDTOByUsername(jwtUsername);

        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidTokenException("Malformed token", token);

        } catch (UsernameNotFoundException e) {
            throw new InvalidTokenException("Invalid token: username does not exist", token);

        }
    }
}