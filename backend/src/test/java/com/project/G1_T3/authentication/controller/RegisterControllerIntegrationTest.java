package com.project.G1_T3.authentication.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.project.G1_T3.authentication.model.RegisterRequest;
import com.project.G1_T3.user.repository.UserRepository;

@Tag("Integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class RegisterControllerIntegrationTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForCommit();
            TestTransaction.end();
        }
        TestTransaction.start();
        userRepository.deleteByUsername(TEST_USERNAME);
        userRepository.deleteByEmail(TEST_EMAIL);
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void registerUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/register");
        RegisterRequest registerRequest = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(uri, registerRequest, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody());

        // Verify that the user was actually saved in the database
        assertTrue(userRepository.existsByUsername(TEST_USERNAME));
        assertTrue(userRepository.existsByEmail(TEST_EMAIL));
    }

    @Test
    public void registerUser_DuplicateUsername_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/register");
        RegisterRequest firstRequest = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, "password123");
        RegisterRequest duplicateRequest = new RegisterRequest(TEST_USERNAME, "test2@example.com", "password456");

        // Register the first user
        restTemplate.postForEntity(uri, firstRequest, String.class);

        // Try to register a user with the same username
        ResponseEntity<String> response = restTemplate.postForEntity(uri, duplicateRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Username is already taken"));
    }

    @Test
    public void registerUser_DuplicateEmail_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/register");
        RegisterRequest firstRequest = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, "password123");
        RegisterRequest duplicateRequest = new RegisterRequest("user2", TEST_EMAIL, "password456");

        // Register the first user
        restTemplate.postForEntity(uri, firstRequest, String.class);

        // Try to register a user with the same email
        ResponseEntity<String> response = restTemplate.postForEntity(uri, duplicateRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Email is already in use"));
    }

    @Test
    public void registerUser_InvalidData_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/register");
        RegisterRequest invalidRequest = new RegisterRequest("", "", ""); // Empty fields

        ResponseEntity<String> response = restTemplate.postForEntity(uri, invalidRequest, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}