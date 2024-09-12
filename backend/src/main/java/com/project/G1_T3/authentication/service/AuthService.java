package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.project.G1_T3.user.model.User;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    public User authenticateUser(String username, String password) throws BadCredentialsException {

        try {

            return (User) authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                    .getPrincipal();

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("The username or password is incorrect", e);
        }
    }
}