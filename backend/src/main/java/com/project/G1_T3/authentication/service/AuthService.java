package com.project.G1_T3.authentication.service;

import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;

public interface AuthService {
    
    LoginResponseDTO authenticateAndGenerateToken(String username, String password);
    UserDTO validateToken(String token);
    boolean verifyEmail(String token);
    User getCurrentUser();
}
