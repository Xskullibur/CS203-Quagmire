package com.project.G1_T3.security.service;

import com.project.G1_T3.user.model.CustomUserDetails;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private SecurityService securityService;

    public void authorizeUserById(UUID userId) {
        CustomUserDetails userDetails = securityService.getAuthenticatedUser();
        if (userDetails == null || !userDetails.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }
    }
}