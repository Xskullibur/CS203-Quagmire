package com.project.G1_T3.matchmaking.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.common.model.Status;

@Tag("Integration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MatchesControllerIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private MatchService matchService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetCurrentMatch_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Match match = new Match();
        match.setId(matchId);
        match.setStatus(Status.IN_PROGRESS);
        match.setPlayer1Id(userId);
        match.setPlayer2Id(UUID.randomUUID());

        when(matchService.getCurrentMatchForUserById(userId)).thenReturn(match);

        mockMvc.perform(get("/matches/current/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matchId").value(matchId.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.player1Id").value(userId.toString()));

        verify(matchService, times(1)).getCurrentMatchForUserById(userId);
    }

    @Test
    public void testGetCurrentMatch_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(matchService.getCurrentMatchForUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/matches/current/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(matchService, times(1)).getCurrentMatchForUserById(userId);
    }

    @Test
    public void testForfeitMatch_Success() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        UUID forfeitedById = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID(); // Assuming the winner is the other player
        Match match = new Match();
        match.setId(matchId);
        match.setStatus(Status.COMPLETED);
        match.setWinnerId(winnerId);
        match.setPlayer1Id(forfeitedById);
        match.setPlayer2Id(winnerId);

        when(matchService.forfeitMatch(matchId, forfeitedById)).thenReturn(match);

        // Act & Assert
        mockMvc.perform(put("/matches/{matchId}/forfeit", matchId)
                .param("forfeitedById", forfeitedById.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(matchId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.winnerId").value(winnerId.toString()))
                .andExpect(jsonPath("$.player1Id").value(forfeitedById.toString()))
                .andExpect(jsonPath("$.player2Id").value(winnerId.toString()));

        verify(matchService, times(1)).forfeitMatch(matchId, forfeitedById);
    }

    @Test
    public void testForfeitMatch_NotFound() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        UUID forfeitedById = UUID.randomUUID();

        when(matchService.forfeitMatch(matchId, forfeitedById)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/matches/{matchId}/forfeit", matchId)
                .param("forfeitedById", forfeitedById.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matchService, times(1)).forfeitMatch(matchId, forfeitedById);
    }

    @Test
    public void testCompleteMatch_Success() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID();
        String score = "3-1";
        Match match = new Match();
        match.setId(matchId);
        match.setStatus(Status.COMPLETED);
        match.setWinnerId(winnerId);
        match.setScore(score);
        match.setPlayer1Id(UUID.randomUUID());
        match.setPlayer2Id(UUID.randomUUID());

        when(matchService.completeMatch(matchId, winnerId, score)).thenReturn(match);

        // Act & Assert
        mockMvc.perform(put("/matches/{matchId}/complete", matchId)
                .param("winnerId", winnerId.toString())
                .param("score", score)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(matchId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.winnerId").value(winnerId.toString()))
                .andExpect(jsonPath("$.score").value(score));

        verify(matchService, times(1)).completeMatch(matchId, winnerId, score);
    }

    @Test
    public void testCompleteMatch_NotFound() throws Exception {
        // Arrange
        UUID matchId = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID();
        String score = "3-1";

        when(matchService.completeMatch(matchId, winnerId, score)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/matches/{matchId}/complete", matchId)
                .param("winnerId", winnerId.toString())
                .param("score", score)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matchService, times(1)).completeMatch(matchId, winnerId, score);
    }
}
