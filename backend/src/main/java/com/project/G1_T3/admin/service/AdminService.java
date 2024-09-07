package com.project.G1_T3.admin.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void getAdminDetails() {
        
    }

}
