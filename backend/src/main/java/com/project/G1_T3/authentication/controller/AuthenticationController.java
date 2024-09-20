package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import com.project.G1_T3.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String jwtUsername = jwtService.extractUsername(jwtToken);

            UserDetails user = userDetailsService.loadUserByUsername(jwtUsername);
            jwtService.validateToken(jwtToken, user);

            final UserDTO userDTO = userService.getUserDTOByUsername(jwtUsername);
            return ResponseEntity.ok(userDTO);

        } catch (UsernameNotFoundException e) {
            throw new InvalidTokenException("Invalid token: username does not exist", token);
        }
    }
}