package com.project.G1_T3.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.authentication.model.RegisterRequest;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/authentication")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                UserRole.PLAYER);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}
