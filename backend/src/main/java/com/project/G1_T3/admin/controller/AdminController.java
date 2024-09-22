package com.project.G1_T3.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.G1_T3.admin.service.AdminService;
import com.project.G1_T3.authentication.model.AdminRegisterRequestDTO;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the admin dashboard!");
    }

    @PostMapping("/get-users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<UserDTO> postMethodName(@Valid @RequestBody AdminRegisterRequestDTO registerRequest) {

        UserDTO userDTO = adminService.registerAdmin(
                registerRequest.getUsername(),
                registerRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

}