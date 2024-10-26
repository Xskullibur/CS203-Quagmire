package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.AuthService;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/validate-token")
    public ResponseEntity<UserDTO> validateToken(@RequestHeader("Authorization") String token) {
        UserDTO userDTO = authService.validateToken(token);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        
        String redirectUrl = "/auth/verification-failed";
        
        try {
            if (authService.verifyEmail(token)) {
                redirectUrl = frontendUrl + "/auth/verification-success";
            }
        } catch (ExpiredJwtException e) {
            redirectUrl = frontendUrl + "/auth/verification-failed";
        }
    
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody String userId) {
        userService.sendVerificationEmailByUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Verification email sent successfully");
    }
}