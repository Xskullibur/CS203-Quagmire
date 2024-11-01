package com.project.G1_T3.admin.controller;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import com.project.G1_T3.authentication.model.AdminRegisterRequestDTO;
import com.project.G1_T3.authentication.service.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    private String adminUsername = "testadmin";
    private String adminEmail = "testadmin@example.com";
    private String adminPassword = "adminPassword";
    private String regularUsername = "testuser";
    private String regularEmail = "testuser@example.com";
    private String regularPassword = "userPassword";

    private String adminToken;
    private String regularUserToken;

    private List<UUID> testUUIDs = new ArrayList<>();

    @BeforeEach
    void setUp() {

        User adminUser = createTestUser(adminUsername, adminEmail, adminPassword, UserRole.ADMIN);
        User regularUser = createTestUser(regularUsername, regularEmail, regularPassword, UserRole.PLAYER);

        // Generate tokens
        adminToken = jwtService.generateToken(adminUser);
        regularUserToken = jwtService.generateToken(regularUser);
    }

    private User createTestUser(String username, String email, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        User addedUser = userRepository.save(user);
        testUUIDs.add(addedUser.getId());

        return addedUser;
    }

    private void createAdditionalUsers() {

        for (int i = 1; i <= 20; i++) {
            String username = "testuser" + i;
            createTestUser(username, username + "@example.com", "password" + i, UserRole.PLAYER);
        }
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllById(testUUIDs);
        testUUIDs.clear();
    }

    private HttpHeaders getAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void getAdminDashboard_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/admin/dashboard");

        HttpEntity<String> entity = new HttpEntity<>(null, getAuthHeaders(adminToken));
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Welcome to the admin dashboard!", result.getBody());
    }

    @Test
    void registerAdmin_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/admin/register-admin");

        AdminRegisterRequestDTO requestDTO = new AdminRegisterRequestDTO();
        requestDTO.setUsername("newadmin");
        requestDTO.setEmail("newadmin@example.com");

        HttpEntity<AdminRegisterRequestDTO> request = new HttpEntity<>(requestDTO, getAuthHeaders(adminToken));
        ResponseEntity<UserDTO> result = restTemplate.exchange(uri, HttpMethod.POST, request, UserDTO.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("newadmin", result.getBody().getUsername());
        assertEquals("newadmin@example.com", result.getBody().getEmail());
        assertEquals(UserRole.ADMIN, result.getBody().getRole());

        userRepository.deleteById(UUID.fromString(result.getBody().getUserId()));
    }

    @Test
    void accessWithoutAuthentication_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/admin/dashboard");

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void accessWithNonAdminAuthentication_Forbidden() throws Exception {
        URI uri = new URI(baseUrl + port + "/admin/dashboard");

        HttpEntity<String> entity = new HttpEntity<>(null, getAuthHeaders(regularUserToken));
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Nested
    class PaginationTests {
        @BeforeEach
        void setupPaginationTests() {
            createAdditionalUsers();
        }

        @Test
        void getPaginatedUsers_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/admin/get-users?page=0&size=10&field=username&order=asc");

            HttpEntity<String> entity = new HttpEntity<>(null, getAuthHeaders(adminToken));
            ResponseEntity<PaginatedResponse<UserDTO>> result = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<PaginatedResponse<UserDTO>>() {
                    });

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(10, result.getBody().getContent().size());
            assertTrue(result.getBody().getTotalElements() > 10);
            assertTrue(isSortedAscending(result.getBody().getContent()));
        }

        @Test
        void getPaginatedUsers_DifferentPageAndSize_Success() throws Exception {
            URI uri = new URI(baseUrl + port + "/admin/get-users?page=1&size=5&field=username&order=desc");

            HttpEntity<String> entity = new HttpEntity<>(null, getAuthHeaders(adminToken));
            ResponseEntity<PaginatedResponse<UserDTO>> result = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<PaginatedResponse<UserDTO>>() {
                    });

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(5, result.getBody().getContent().size());
            assertTrue(result.getBody().getTotalElements() > 5);
            assertTrue(isSortedDescending(result.getBody().getContent()));
        }

    }

    private boolean isSortedAscending(List<UserDTO> users) {
        for (int i = 0; i < users.size() - 1; i++) {
            if (users.get(i).getUsername().compareTo(users.get(i + 1).getUsername()) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isSortedDescending(List<UserDTO> users) {
        for (int i = 0; i < users.size() - 1; i++) {
            if (users.get(i).getUsername().compareTo(users.get(i + 1).getUsername()) < 0) {
                return false;
            }
        }
        return true;
    }

    // Helper class to handle pagination in responses
    public static class PaginatedResponse<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int number;
        private int size;

        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}