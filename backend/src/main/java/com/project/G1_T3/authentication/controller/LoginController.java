package com.project.G1_T3.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.authentication.service.AuthService;

@RestController
@RequestMapping("/authentication")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        LoginResponseDTO response = authService.authenticateAndGenerateToken(
                loginRequest.getUsername(),
                loginRequest.getPassword());
                
        return ResponseEntity.ok().body(response);
    }
}