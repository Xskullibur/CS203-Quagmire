package com.project.G1_T3.authentication.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.authentication.model.RegisterRequest;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.UserService;

@RestController
@RequestMapping("/authentication")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerProfileService playerProfileService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {

        userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword());

        UserDTO userDTO = userService.getUserDTOByUsername(registerRequest.getUsername());
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setUserId(UUID.fromString(userDTO.getUserId()));
        playerProfileService.save(playerProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}
