package com.project.G1_T3.authentication.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponseDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Set up test data
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@test.com");
        testUser.setRole(UserRole.PLAYER);
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Delete only the test user created for this test
        if (testUser != null && testUser.getId() != null) {
            userRepository.deleteById(testUser.getId());
        }
    }

    @Test
    void loginUser_ValidCredentials_ReturnsOkWithToken() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/login");
        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity(uri, loginRequest,
                LoginResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUser().getUsername());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/login");
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(uri, loginRequest, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginUser_NonexistentUser_ReturnsUnauthorized() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/login");
        LoginRequest loginRequest = new LoginRequest("nonexistentuser", "password");

        ResponseEntity<String> response = restTemplate.postForEntity(uri, loginRequest, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginUser_EmptyCredentials_ReturnsBadRequest() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/login");
        LoginRequest loginRequest = new LoginRequest("", "");

        ResponseEntity<String> response = restTemplate.postForEntity(uri, loginRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}