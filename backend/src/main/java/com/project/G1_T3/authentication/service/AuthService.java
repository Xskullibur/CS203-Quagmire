package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
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

    public User authenticateUser(String username, String password) throws BadCredentialsException {

        try {

            return (User) authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                    .getPrincipal();

        } catch (AuthenticationException e) {

            throw new BadCredentialsException("The username or password is incorrect", e);
        }
    }

    public UserDTO validateToken(String token) {
        try {
            String jwtToken = token.substring(7); // Remove "Bearer " prefix
            String jwtUsername = jwtService.extractUsername(jwtToken);

            UserDetails user = userDetailsService.loadUserByUsername(jwtUsername);
            jwtService.validateToken(jwtToken, user);

            return userService.getUserDTOByUsername(jwtUsername);

        } catch (UsernameNotFoundException e) {
            throw new InvalidTokenException("Invalid token: username does not exist", token);
        }
    }
}