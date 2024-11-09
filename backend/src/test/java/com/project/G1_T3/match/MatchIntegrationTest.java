package com.project.G1_T3.match;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.match.repository.MatchRepository;

import jakarta.transaction.Transactional;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback(false)
public class MatchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

	@Autowired
    private PlayerProfileRepository playerProfileRepository;

	@Autowired
	private TournamentRepository tournamentRepository;

	@Autowired
	private StageRepository stageRepository;

	@Autowired
	private RoundRepository roundRepository;

	@Autowired
	private MatchRepository matchRepository;

    private UUID tournamentId;
    private UUID player1Id;
    private UUID player2Id;
    private UUID stageId;
    private UUID roundId;
    private UUID matchId;

    // private final List<UUID> playerIds = List.of(
    //         UUID.fromString("11111111-1111-1111-1111-111111111111"),
    //         UUID.fromString("11111111-1111-1111-1111-111111111112"),
    //         UUID.fromString("11111111-1111-1111-1111-111111111113"),
    //         UUID.fromString("11111111-1111-1111-1111-111111111114"),
    //         UUID.fromString("11111111-1111-1111-1111-111111111115"),
    //         UUID.fromString("11111111-1111-1111-1111-111111111116"));
            
    // @BeforeEach
    // public void setUp() throws Exception {

    //     objectMapper.registerModule(new JavaTimeModule());
    //     objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //     // Log tournament creation
    //     System.out.println("Creating tournament...");
    //     tournamentId = createTournament();
    //     assertNotNull("Tournament ID should not be null after creation", tournamentId);

    //     // Add pre-existing players to the tournament
    //     System.out.println("Adding players to tournament...");
    //     for (UUID playerId : playerIds) {
	// 		PlayerProfile playerProfile = createAndSavePlayerProfile(); // Create and save profile
	// 		assertTrue(playerProfileRepository.existsById(playerProfile.getProfileId())); // Check existence
	// 		// assertNotNull("Player profile should exist in the repository", playerProfileRepository.findById(playerId).orElse(null));
    //         addPlayerToTournament(tournamentId, playerProfile.getProfileId());
    //         System.out.println("Added player with ID: " + playerProfile.getProfileId());
    //     }

	// 	saveTournament(tournamentId);

    //     // // Assign player1Id and player2Id from playerIds list
    //     // player1Id = playerIds.get(0);
    //     // player2Id = playerIds.get(1);

    //     // Start the tournament
    //     System.out.println("Starting tournament...");
    //     startTournament(tournamentId);

    //     // Retrieve stage, round, and match information
    //     System.out.println("Retrieving stage ID...");
    //     stageId = getStageId(tournamentId);
    //     assertNotNull("Stage ID should not be null", stageId);

    //     System.out.println("Retrieving round ID...");
    //     roundId = getRoundId(stageId);
    //     assertNotNull("Round ID should not be null", roundId);

    //     System.out.println("Retrieving match ID...");
    //     matchId = getMatchId(roundId);
    //     assertNotNull("Match ID should not be null", matchId);
    // }
	private List<UUID> playerIds; // Initialize this list in setUp

	@BeforeEach
	public void setUp() throws Exception {

		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Log tournament creation
		System.out.println("Creating tournament...");
		tournamentId = createTournament();
		assertNotNull("Tournament ID should not be null after creation", tournamentId);

		// Initialize the playerIds list
		playerIds = new ArrayList<>();
		float currentRating = 2500F;

		// Add pre-existing players to the tournament
		System.out.println("Adding players to tournament...");
		for (int i = 0; i < 4; i++) { // Adding 6 players as per your original list length
			System.out.println("HELLO: " + i);
			currentRating += 100;
			PlayerProfile playerProfile = createAndSavePlayerProfile(currentRating); // Create and save profile
			assertTrue(playerProfileRepository.existsById(playerProfile.getProfileId())); // Check existence
			addPlayerToTournament(tournamentId, playerProfile.getProfileId());
			
			// Store the generated player ID in the list
			playerIds.add(playerProfile.getProfileId());
			System.out.println("Added player with ID: " + playerProfile.getProfileId());
		}
		// saveTournament(tournamentId);

		// Assign player1Id and player2Id from dynamically created playerIds list
		player1Id = playerIds.get(0);
		player2Id = playerIds.get(3);

		// Start the tournament
		System.out.println("Starting tournament...");
		startTournament(tournamentId);

		Tournament tournament = tournamentRepository.findById(tournamentId)
        	.orElseThrow(() -> new RuntimeException("Tournament not found"));
		System.out.println("NUMSTAGES = " + tournament.getStages().size());

		// Retrieve stage, round, and match information
		System.out.println("Retrieving stage ID...");
		stageId = getStageId(tournamentId);
		assertNotNull("Stage ID should not be null", stageId);

		System.out.println("Retrieving round ID...");
		roundId = getRoundId(tournamentId, stageId);
		assertNotNull("Round ID should not be null", roundId);

		System.out.println("Retrieving match ID...");
		matchId = getMatchId(roundId);
		assertNotNull("Match ID should not be null", matchId);
	}


    @AfterEach
    void tearDown() {
		matchRepository.deleteAll();
		roundRepository.deleteAll();
		stageRepository.deleteAll();
		tournamentRepository.deleteAll();
        playerProfileRepository.deleteAll();
    }

	// Utility method to create and save a PlayerProfile, allowing the database to generate profileId
	private PlayerProfile createAndSavePlayerProfile(float current_rating) {
		PlayerProfile playerProfile = new PlayerProfile();
		// Set other fields as needed, except profileId
		playerProfile.setFirstName("Test");
		playerProfile.setLastName("Player");
		playerProfile.setCurrentRating(current_rating);

		playerProfile = playerProfileRepository.saveAndFlush(playerProfile); // Save and flush to get generated ID
		return playerProfile; // Return saved profile with generated profileId
	}

	// private void saveTournament(UUID tournamentId) {
	// 	Tournament tournament = tournamentRepository.findById(tournamentId)
    //     	.orElseThrow(() -> new RuntimeException("Tournament not found"));
	// 	tournamentRepository.save(tournament);
	// }

    // Utility method for creating a tournament
    private UUID createTournament() throws Exception {
        // Mock JSON string for tournament creation, with all required fields populated
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
                "tournament", // Name of the part
                "tournament.json", // Original file name
                MediaType.APPLICATION_JSON_VALUE, // MIME type for JSON
                tournamentJson.getBytes() // Content as byte array
        );

        // Mock a file for the photo (if required by the endpoint)
        MockMultipartFile mockFile = new MockMultipartFile("photo", "", "image/jpeg", new byte[0]);

        MvcResult result = mockMvc.perform(
                multipart("/tournament/create")
                        .file(tournamentJsonFile) // Add JSON data as a file part
                        .file(mockFile)           // Add photo part if required
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Set as multipart/form-data
                        // .file(new MockMultipartFile("photo", "", "image/jpeg", new byte[0])) // No photo, empty bytes
                        // .param(tournamentJson))
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        Tournament createdTournament = objectMapper.readValue(response, Tournament.class);
        return createdTournament.getId();
    }

    // Utility method to add a player
    private void addPlayerToTournament(UUID tournamentId, UUID profileId) throws Exception {
        MvcResult result = mockMvc.perform(put("/tournament/" + tournamentId + "/players/" + profileId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
				.andReturn();

		// Log the response to see details if there are errors
		System.out.println("Add Player Response: " + result.getResponse().getContentAsString());
    }

    // Utility method to start the tournament
    private void startTournament(UUID tournamentId) throws Exception {
        mockMvc.perform(put("/tournament/" + tournamentId + "/start"))
                .andExpect(status().isOk());
    }

    // Utility method to get the stage ID
    private UUID getStageId(UUID tournamentId) throws Exception {
        MvcResult result = mockMvc.perform(get("/tournament/" + tournamentId + "/stage/allStages"))
                .andExpect(status().isOk())
                .andReturn();

        List<Stage> stages = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return stages.get(0).getStageId(); // Assuming only one stage
    }

    // Utility method to get the round ID
    private UUID getRoundId(UUID tournamentId, UUID stageId) throws Exception {
        MvcResult result = mockMvc.perform(get("/tournament/" + tournamentId + "/stage/" + stageId + "/round/allRounds"))
                .andExpect(status().isOk())
                .andReturn();

		List<Round> rounds = objectMapper.readValue(result.getResponse().getContentAsString(),
		new TypeReference<>() {
		});
        return rounds.get(0).getRoundId(); // Assuming only one stage

        // Round round = objectMapper.readValue(result.getResponse().getContentAsString(), Round.class);
        // return round.getRoundId(); // Assuming only one round
    }

    // Utility method to get the match ID
    private UUID getMatchId(UUID roundId) throws Exception {
        MvcResult result = mockMvc.perform(get("/match/round/" + roundId))
                .andExpect(status().isOk())
                .andReturn();

        List<Match> matches = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return matches.get(0).getId(); // Assuming only one match
    }

    // Test for completing the match
    // @Test
    // public void testCompleteMatch() throws Exception {
    //     // Set up MatchDTO with winner and score details
    //     MatchDTO matchDTO = new MatchDTO();
    //     matchDTO.setPlayer1Id(player1Id);
    //     matchDTO.setPlayer2Id(player2Id);
    //     matchDTO.setWinnerId(player1Id);
    //     matchDTO.setScore("6-4");

    //     String matchJson = objectMapper.writeValueAsString(matchDTO);

    //     // Perform the request to complete the match
    //     mockMvc.perform(put("/match/" + matchId + "/complete")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(matchJson))
    //             .andExpect(status().isOk())
    //             .andExpect(content().string("Match completed"));
    // }

	@Test
	public void testCompleteMatch() throws Exception {
		// Set up MatchDTO with player details to start the match
		MatchDTO startMatchDTO = new MatchDTO();
		startMatchDTO.setPlayer1Id(player1Id);
		startMatchDTO.setPlayer2Id(player2Id);
	
		String startMatchJson = objectMapper.writeValueAsString(startMatchDTO);
	
		// Start the match by sending the start request
		mockMvc.perform(put("/match/" + matchId + "/start")
				.contentType(MediaType.APPLICATION_JSON)
				.content(startMatchJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Match started"));
	
		// Set up MatchDTO with winner and score details to complete the match
		MatchDTO completeMatchDTO = new MatchDTO();
		completeMatchDTO.setPlayer1Id(player1Id);
		completeMatchDTO.setPlayer2Id(player2Id);
		completeMatchDTO.setWinnerId(player1Id);
		completeMatchDTO.setScore("6-4");
	
		String completeMatchJson = objectMapper.writeValueAsString(completeMatchDTO);
	
		// Perform the request to complete the match
		mockMvc.perform(put("/match/" + matchId + "/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(completeMatchJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Match completed"));
	}
	
}
