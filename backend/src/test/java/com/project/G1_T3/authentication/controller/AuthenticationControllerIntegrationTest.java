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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.authentication.model.LoginResponseDTO;
import com.project.G1_T3.authentication.service.AuthService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    private User testUser;
    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@test.com");
        testUser.setRole(UserRole.PLAYER);
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);

        LoginResponseDTO loginResponseDTO = authService.authenticateAndGenerateToken(testUser.getUsername(), "password");

        validToken = loginResponseDTO.getToken();
        invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUdXR1cnUxIiwiaWF0IjoxNzI1NTUxMjI5LCJleHAiOjE3MjY0MTUyMjl9.e7auXpTzaaiq8gsaGlOfM30DS9VVJ0N_qaKtCEV7wra";
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(testUser.getId());
    }

    @Test
    void validateToken_ValidToken_ReturnsUserDTO() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/validate-token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + validToken);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getUsername(), response.getBody().getUsername());
    }

    @Test
    void validateToken_InvalidToken_ReturnsUnauthorized() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/validate-token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + invalidToken);

        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void validateToken_MissingToken_ReturnsBadRequest() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/validate-token");
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void validateToken_MalformedToken_ReturnsBadRequest() throws Exception {
        URI uri = new URI(baseUrl + port + "/authentication/validate-token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer malformed.token");

        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}