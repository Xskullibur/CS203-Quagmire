package com.project.G1_T3.security.validator;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.project.G1_T3.common.exception.EmailNotVerifiedException;
import com.project.G1_T3.user.model.CustomUserDetails;

@Component("emailVerificationValidator")
public class EmailVerificationValidator {
    
    public boolean isEmailVerified(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AccessDeniedException("Invalid authentication type");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        if (!userDetails.getUser().isEmailVerified()) {
            throw new EmailNotVerifiedException(userDetails.getUsername());
        }
        
        return true;
    }
}
