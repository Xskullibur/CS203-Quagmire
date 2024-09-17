package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        Optional<User> user = jwtService.validateTokenAndGetUser(token);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.map(UserDTO::fromUser));
        } else {
            logger.warn("Invalid token for user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}