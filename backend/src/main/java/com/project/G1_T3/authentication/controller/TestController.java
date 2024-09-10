package com.project.G1_T3.authentication.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.G1_T3.authentication.service.TestService;

@RestController
@RequestMapping("/test")
@Profile("test")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    @PostMapping("/remove-test-user")
    public ResponseEntity<String> removeTestUser() {
        logger.info("Received request to remove test user");
        try {
            String result = testService.removeTestUser();
            logger.info("Result of removing test user: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error removing test user", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}