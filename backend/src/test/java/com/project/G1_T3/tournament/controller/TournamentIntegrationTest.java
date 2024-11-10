package com.project.G1_T3.tournament.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class TournamentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String adminUsername = "tuturu";
    private final String adminEmail = "hi@hi";
    private final String adminPassword = "P@ssw0rd";
    private User adminUser;
    private String adminToken;

    @BeforeEach
    void setUp() {

        adminUser = createTestUser(adminUsername, adminEmail, adminPassword, UserRole.ADMIN);

        // Generate tokens
        adminToken = jwtService.generateToken(adminUser);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private User createTestUser(String username, String email, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        User addedUser = userRepository.save(user);

        return addedUser;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
//     @WithMockUser(username = "tuturu", password = "P@ssw0rd", roles = "ADMIN")
    public void testCreateTournament() throws Exception {
        // JSON payload for tournament creation
        String tournamentJson = "{ " +
                "\"name\": \"Integration Test Tournament\", " +
                "\"location\": \"New York\", " +
                "\"startDate\": \"2025-01-01T10:00:00\", " +
                "\"endDate\": \"2025-01-10T18:00:00\", " +
                "\"deadline\": \"2025-12-20T23:59:59\", " +
                "\"maxParticipants\": 16, " +
                "\"description\": \"An integration test tournament\", " +
                "\"status\": \"SCHEDULED\", " +
                "\"stageDTOs\": [ " +
                        "{ " +
                        "\"stageName\": \"Only Stage\", " +
                        "\"startDate\": \"2025-10-01T10:00:00\", " +
                        "\"endDate\": \"2025-10-02T18:00:00\", " +
                        "\"format\": \"SINGLE_ELIMINATION\", " +
                        "\"status\": \"SCHEDULED\" " +
                        "} " +
                "] " +
                "}";

        // Wrap JSON data as a MockMultipartFile
        MockMultipartFile tournamentJsonFile = new MockMultipartFile(
                "tournament", // Name of the part (should match the expected part name in the controller)
                "tournament.json", // Original file name (can be anything)
                MediaType.APPLICATION_JSON_VALUE, // MIME type for JSON
                tournamentJson.getBytes() // Content as byte array
        );

        // Mock a file for the photo (if required by the endpoint, otherwise it can be
        // removed)
        MockMultipartFile mockFile = new MockMultipartFile("photo", "", "image/jpeg", new byte[0]);

        // Perform the request to create the tournament with authentication
        MvcResult result = mockMvc.perform(
                multipart("/tournament/create")
                        .file(tournamentJsonFile) // Add JSON data as a file part
                        .file(mockFile)
                        .header("Authorization", "Bearer " + adminToken)
                        // .param("tournament", tournamentJson)
                        // .with(httpBasic(adminUsername, adminPassword)) // Authentication
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()) // Expect a 200 OK status if successful
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response content: " + responseContent);
        assertNotNull(responseContent, "Response content should not be null");

        // Optionally, deserialize the response and validate the content
        Tournament createdTournament = objectMapper.readValue(responseContent, Tournament.class);
        assertNotNull(createdTournament.getId(), "Tournament ID should not be null");
        assertEquals("Integration Test Tournament", createdTournament.getName());
        assertEquals("New York", createdTournament.getLocation());
        assertEquals(16, createdTournament.getMaxParticipants());
    }


    @Test
    public void testAddPlayersToTournament() throws Exception {
        // Step 1: Create User and Player Profile for Player 1
        User player1User = createUser("player1", "password1");
        UUID player1Id = createPlayerProfile("John", "Doe", player1User);

        // Step 2: Create User and Player Profile for Player 2
        User player2User = createUser("player2", "password2");
        UUID player2Id = createPlayerProfile("Jane", "Smith", player2User);

        // Step 3: Authenticate and Add Players to Tournament
        addPlayerToTournament(tournamentId, player1Id, player1User.getUsername(), "password1");
        addPlayerToTournament(tournamentId, player2Id, player2User.getUsername(), "password2");
    }

    private User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(UserRole.PLAYER);
        user.setEmail(username + "@example.com");

        return userRepository.save(user);
    }

    private UUID createPlayerProfile(String firstName, String lastName, User user) {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setFirstName(firstName);
        playerProfile.setLastName(lastName);
        playerProfile.setUser(user); // Associate with the User
        playerProfile.setGlickoRating(1500);
        playerProfile.setRatingDeviation(350.0f);
        playerProfile.setVolatility(0.06f);

        PlayerProfile savedProfile = playerProfileService.save(playerProfile);
        assertNotNull(savedProfile.getProfileId(), "Player profile ID should not be null");
        return savedProfile.getProfileId();
    }

    private void addPlayerToTournament(UUID tournamentId, UUID playerId, String username, String password) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tournament/" + tournamentId + "/players/" + playerId)
                .with(httpBasic(username, password))) // Authenticate as the player
                .andExpect(status().isOk());
    }

    

  
}
