package com.project.G1_T3.user.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.model.UpdateEmailDTO;
import com.project.G1_T3.user.model.UpdatePasswordDTO;
import com.project.G1_T3.authentication.service.JwtService;

@Tag("Integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

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
    private JwtService jwtService;

    private String username = "testuser";
    private String email = "testuser@example.com";
    private String password = "password123";
    private String token;
    private List<UUID> testUUIDs = new ArrayList<>();

    @BeforeEach
    void setUp() {
        User user = createTestUser(username, email, password, UserRole.PLAYER);
        token = jwtService.generateToken(user);
        createAdditionalUsers();
    }

    private User createTestUser(String username, String email, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        testUUIDs.add(savedUser.getId());
        return savedUser;
    }

    private void createAdditionalUsers() {
        createTestUser("john_doe", "john@example.com", "password", UserRole.PLAYER);
        createTestUser("jane_doe", "jane@example.com", "password", UserRole.PLAYER);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllById(testUUIDs);
        testUUIDs.clear();
    }

    private HttpHeaders getAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Nested
    class UserInfoTests {
        @Test
        void getUserInfo_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/users");

            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders(token));
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                UserDTO.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(username, response.getBody().getUsername());
            assertEquals(email, response.getBody().getEmail());
        }

        @Test
        void getUserInfo_Unauthorized() throws Exception {
            URI uri = new URI(baseUrl + port + "/users");

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    class SearchUsersTests {
        @Test
        void searchUsers_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/search?username=john");

            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders(token));
            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserDTO>>() {}
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().isEmpty());
            assertTrue(response.getBody().stream()
                .anyMatch(user -> user.getUsername().contains("john")));
        }

        @Test
        void searchUsers_NoResults() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/search?username=nonexistent");

            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders(token));
            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserDTO>>() {}
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }
    }

    @Nested
    class UpdatePasswordTests {
        @Test
        void updatePassword_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/update-password");

            UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
            updatePasswordDTO.setCurrentPassword(password);
            updatePasswordDTO.setNewPassword("newPassword123");

            HttpEntity<UpdatePasswordDTO> request = new HttpEntity<>(updatePasswordDTO, getAuthHeaders(token));
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Password updated successfully.", response.getBody());

            // Verify password was actually updated
            User updatedUser = userRepository.findByUsername(username).get();
            assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPasswordHash()));
        }

        @Test
        void updatePassword_WrongCurrentPassword() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/update-password");

            UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
            updatePasswordDTO.setCurrentPassword("wrongPassword");
            updatePasswordDTO.setNewPassword("newPassword123");

            HttpEntity<UpdatePasswordDTO> request = new HttpEntity<>(updatePasswordDTO, getAuthHeaders(token));
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                String.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class UpdateEmailTests {
        @Test
        void updateEmail_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/update-email");

            UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
            updateEmailDTO.setPassword(password);
            updateEmailDTO.setNewEmail("newemail@example.com");

            HttpEntity<UpdateEmailDTO> request = new HttpEntity<>(updateEmailDTO, getAuthHeaders(token));
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Email updated successfully.", response.getBody());

            // Verify email was actually updated
            User updatedUser = userRepository.findByUsername(username).get();
            assertEquals("newemail@example.com", updatedUser.getEmail());
        }

        @Test
        void updateEmail_WrongPassword() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/update-email");

            UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
            updateEmailDTO.setPassword("wrongPassword");
            updateEmailDTO.setNewEmail("newemail@example.com");

            HttpEntity<UpdateEmailDTO> request = new HttpEntity<>(updateEmailDTO, getAuthHeaders(token));
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                request,
                String.class
            );

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    class EmailVerificationTests {
        @Test
        void resendEmailVerification_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/resend-email-verification");

            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders(token));
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Verification email has been resent.", response.getBody());
        }

        @Test
        void resendEmailVerification_Unauthorized() throws Exception {
            URI uri = new URI(baseUrl + port + "/users/resend-email-verification");

            ResponseEntity<String> response = restTemplate.postForEntity(uri, null, String.class);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}