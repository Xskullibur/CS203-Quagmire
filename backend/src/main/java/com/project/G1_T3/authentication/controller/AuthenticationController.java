package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.authentication.service.AuthService;
import com.project.G1_T3.user.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/validate-token")
    public ResponseEntity<UserDTO> validateToken(@RequestHeader("Authorization") String token) {
        UserDTO userDTO = authService.validateToken(token);
        return ResponseEntity.ok(userDTO);
    }
}