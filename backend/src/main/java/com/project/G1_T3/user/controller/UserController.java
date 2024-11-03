package com.project.G1_T3.user.controller;

import com.project.G1_T3.authentication.model.ResetPasswordDTO;
import com.project.G1_T3.user.model.UpdateEmailDTO;
import com.project.G1_T3.user.model.UpdatePasswordDTO;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.UserService;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsersByUsername(@RequestParam String username) {
        List<User> users = userService.findUsersByUsernameContaining(username);

        // Convert User entities to UserDTOs
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }
    
    @GetMapping
    public ResponseEntity<UserDTO> getUserInfo(Authentication authentication) {
        // Retrieve the username of the authenticated user
        String username = authentication.getName();
        
        // Fetch the user information
        UserDTO userDTO = userService.getUserInfo(username);
        
        // Return the user details as a response
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/resend-email-verification")
    public ResponseEntity<String> resendEmailVerification(Authentication authentication) {
        String username = authentication.getName(); // Get username from authentication context
        System.out.println(username);
        
        Optional<User> optionalUser = userService.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Directly call the sendVerificationEmail method from UserService
        userService.sendVerificationEmail(user);
        
        return ResponseEntity.ok("Verification email has been resent.");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
        String username = authentication.getName(); 
        System.out.println(username);
        userService.updatePassword(username, updatePasswordDTO);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @PutMapping("/update-email")
    public ResponseEntity<String> updateEmail(Authentication authentication, @RequestBody @Valid UpdateEmailDTO updateEmailDTO) {
        String username = authentication.getName();
        boolean isUpdated = userService.updateEmail(username, updateEmailDTO);

        return isUpdated ? ResponseEntity.ok("Email updated successfully.") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
    }
}
