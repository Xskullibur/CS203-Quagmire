package com.project.G1_T3.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.service.PasswordGeneratorService;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.service.UserService;

import jakarta.transaction.Transactional;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @Transactional
    public void registerAdmin(String username, String email) {

        String password = passwordGeneratorService.generatePassword();
        userService.registerUser(username, email, password, UserRole.ADMIN);
        logger.info("Admin registered successfully: {}:{}", username, password);
    }

    public void getAdminDetails() {

    }

}
