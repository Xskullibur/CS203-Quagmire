package com.project.G1_T3.tournament.controller;


import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.player.service.PlayerProfileService;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerProfileService playerProfileService;

    private final String adminUsername = "tuturu";
    private final String adminPassword = "P@ssw0rd";

    UUID tournamentId;

    @BeforeEach
    void setupAdminUser() {
        // Check if admin user already exists to avoid duplicates
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword)); // Encode the password
            adminUser.setRole(UserRole.ADMIN); // Assuming 'Role.ADMIN' is an enum or constant for the admin role
            adminUser.setEmail("admin@example.com"); // Set a valid email if required
            userRepository.save(adminUser); // Save admin user to the database
        }
    }

    @BeforeEach
    public void testCreateTournament() throws Exception {
        // JSON payload for tournament creation
        String tournamentJson = "{ " +
                "\"name\": \"Integration Test Tournament\", " +
                "\"location\": \"New York\", " +
                "\"startDate\": \"2024-01-01T10:00:00\", " +
                "\"endDate\": \"2024-01-10T18:00:00\", " +
                "\"deadline\": \"2023-12-20T23:59:59\", " +
                "\"maxParticipants\": 16, " +
                "\"description\": \"An integration test tournament\", " +
                "\"status\": \"SCHEDULED\", " +
                "\"stageDTOs\": null" +
                "}";

        // Perform the request to create the tournament with authentication
        // Wrap JSON in a MockMultipartFile to simulate a multipart request
        MockMultipartFile tournamentJsonPart = new MockMultipartFile("tournament", "", "application/json",
                tournamentJson.getBytes());

        // Perform the request to create the tournament with authentication
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/tournament/create")
                .file(tournamentJsonPart)
                .with(httpBasic(adminUsername, adminPassword)) // Authentication
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()) // Expect a 200 OK status if successful
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent, "Response content should not be null");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Tournament createdTournament = objectMapper.readValue(responseContent, Tournament.class);
        tournamentId = createdTournament.getId();

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
