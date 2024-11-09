package com.project.G1_T3.match;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.tournament.model.Tournament;
import jakarta.transaction.Transactional;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MatchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private UUID tournamentId;
    private UUID player1Id;
    private UUID player2Id;
    private UUID stageId;
    private UUID roundId;
    private UUID matchId;


    private final List<UUID> playerIds = List.of(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("11111111-1111-1111-1111-111111111112"),
            UUID.fromString("11111111-1111-1111-1111-111111111113"),
            UUID.fromString("11111111-1111-1111-1111-111111111114"),
            UUID.fromString("11111111-1111-1111-1111-111111111115"),
            UUID.fromString("11111111-1111-1111-1111-111111111116"));
            
    @BeforeEach
    public void setUp() throws Exception {
        // Log tournament creation
        System.out.println("Creating tournament...");
        tournamentId = createTournament();
        assertNotNull("Tournament ID should not be null after creation", tournamentId);

        // Add pre-existing players to the tournament
        System.out.println("Adding players to tournament...");
        for (UUID playerId : playerIds) {
            addPlayerToTournament(tournamentId, playerId);
            System.out.println("Added player with ID: " + playerId);
        }

        // Start the tournament
        System.out.println("Starting tournament...");
        startTournament(tournamentId);

        // Retrieve stage, round, and match information
        System.out.println("Retrieving stage ID...");
        stageId = getStageId(tournamentId);
        assertNotNull("Stage ID should not be null", stageId);

        System.out.println("Retrieving round ID...");
        roundId = getRoundId(stageId);
        assertNotNull("Round ID should not be null", roundId);

        System.out.println("Retrieving match ID...");
        matchId = getMatchId(roundId);
        assertNotNull("Match ID should not be null", matchId);
    }

    // Utility method for creating a tournament
    private UUID createTournament() throws Exception {
        // Mock JSON string for tournament creation, with all required fields populated
        String tournamentJson = "{ " +
                "\"name\": \"Test Tournament\", " +
                "\"location\": \"New York\", " +
                "\"startDate\": \"2024-01-01T10:00:00\", " +
                "\"endDate\": \"2024-01-10T18:00:00\", " +
                "\"deadline\": \"2023-12-20T23:59:59\", " +
                "\"maxParticipants\": 16, " +
                "\"description\": \"A test tournament for integration testing\", " +
                "\"status\": \"SCHEDULED\", " +
                "}";

        MvcResult result = mockMvc.perform(
                multipart("/tournament/create")
                        .file(new MockMultipartFile("photo", "", "image/jpeg", new byte[0])) // No photo, empty bytes
                        .param(tournamentJson))
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        Tournament createdTournament = new ObjectMapper().readValue(response, Tournament.class);
        return createdTournament.getId();
    }

    // Utility method to add a player
    private void addPlayerToTournament(UUID tournamentId, UUID profileId) throws Exception {
        mockMvc.perform(post("/tournament/" + tournamentId + "/addPlayer")
                .param("profileId", profileId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Utility method to start the tournament
    private void startTournament(UUID tournamentId) throws Exception {
        mockMvc.perform(put("/tournament/" + tournamentId + "/start"))
                .andExpect(status().isOk());
    }

    // Utility method to get the stage ID
    private UUID getStageId(UUID tournamentId) throws Exception {
        MvcResult result = mockMvc.perform(get("/tournament/" + tournamentId + "/allStages"))
                .andExpect(status().isOk())
                .andReturn();

        List<Stage> stages = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return stages.get(0).getStageId(); // Assuming only one stage
    }

    // Utility method to get the round ID
    private UUID getRoundId(UUID stageId) throws Exception {
        MvcResult result = mockMvc.perform(get("/round/" + stageId))
                .andExpect(status().isOk())
                .andReturn();

        Round round = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Round.class);
        return round.getRoundId(); // Assuming only one round
    }

    // Utility method to get the match ID
    private UUID getMatchId(UUID roundId) throws Exception {
        MvcResult result = mockMvc.perform(get("/match/round/" + roundId))
                .andExpect(status().isOk())
                .andReturn();

        List<Match> matches = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        return matches.get(0).getId(); // Assuming only one match
    }

    // Test for completing the match
    @Test
    public void testCompleteMatch() throws Exception {
        // Set up MatchDTO with winner and score details
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setPlayer1Id(player1Id);
        matchDTO.setPlayer2Id(player2Id);
        matchDTO.setWinnerId(player1Id);
        matchDTO.setScore("6-4");

        String matchJson = new ObjectMapper().writeValueAsString(matchDTO);

        // Perform the request to complete the match
        mockMvc.perform(put("/match/" + matchId + "/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Match completed"));
    }
}
