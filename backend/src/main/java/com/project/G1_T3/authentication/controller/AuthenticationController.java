package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.AuthServiceImpl;
import com.project.G1_T3.user.model.UserDTO;
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
    private AuthServiceImpl authService;

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

        boolean verified = authService.verifyEmail(token);
        String redirectUrl = verified
                ? frontendUrl + "/auth/verification-success"
                : frontendUrl + "/auth/verification-failed";

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}