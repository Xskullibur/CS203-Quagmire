package com.project.G1_T3.authentication.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.player.model.User;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    public Optional<User> authenticateUser(String username, String password) {

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (auth.isAuthenticated()) {
                return Optional.of((User) auth.getPrincipal());
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("The username or password is incorrect");
        }

        return Optional.empty();
    }
}