package com.project.G1_T3.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.G1_T3.authentication.model.ResetPasswordDTO;
import com.project.G1_T3.user.service.UserService;


@RestController
@RequestMapping("/authentication")
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Password updated successfully.");
    }
}
