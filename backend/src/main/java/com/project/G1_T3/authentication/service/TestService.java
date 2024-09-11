package com.project.G1_T3.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.G1_T3.player.repository.UserRepository;

@Service
@Profile("test")
public class TestService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String removeTestUser() {
        try {
            long deletedCount = userRepository.deleteByUsername("testuser");
            if (deletedCount == 0) {
                return "No user found with username 'testuser'";
            }
            return "User deleted successfully. Deleted count: " + deletedCount;
        } catch (Exception e) {
            return "Error deleting user: " + e.getMessage();
        }
    }
}