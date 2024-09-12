package com.project.G1_T3.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.authentication.service.AuthService;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;

@RestController
@RequestMapping("/authentication")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        User user = authService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword());

        String token = jwtService.generateToken(user);
        UserDTO userDTO = UserDTO.fromUser(user);
        LoginResponseDTO response = new LoginResponseDTO(userDTO, token);

        return ResponseEntity.ok().body(response);
    }
}