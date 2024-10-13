package com.project.G1_T3.matchmaking.controller;

import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.matchmaking.model.QueueRequest;
import com.project.G1_T3.matchmaking.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.HashMap;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SoloQueueControllerTest {

    private SoloQueueController soloQueueController;

    @Mock
    private MatchmakingService matchmakingService;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @BeforeEach
    void setUp() {
        soloQueueController = new SoloQueueController(matchmakingService, playerProfileRepository);
        when(headerAccessor.getSessionAttributes()).thenReturn(new HashMap<>());
    }

    @Test
    void testAddToQueue_Success() {
        UUID playerId = UUID.randomUUID();
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setUserId(playerId);

        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId(playerId.toString());
        queueRequest.setLocation(new Location(10.0, 20.0));

        when(playerProfileRepository.findByUserId(playerId)).thenReturn(playerProfile);

        soloQueueController.addToQueue(queueRequest, headerAccessor);

        verify(playerProfileRepository).findByUserId(playerId);
        verify(matchmakingService).addPlayerToQueue(eq(playerProfile), eq(10.0), eq(20.0));
        assertFalse(headerAccessor.getSessionAttributes().containsKey("disconnect"));
    }

    @Test
    void testAddToQueue_PlayerNotFound() {
        UUID playerId = UUID.randomUUID();
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId(playerId.toString());
        queueRequest.setLocation(new Location(10.0, 20.0));

        when(playerProfileRepository.findByUserId(playerId)).thenReturn(null);

        soloQueueController.addToQueue(queueRequest, headerAccessor);

        verify(playerProfileRepository).findByUserId(playerId);
        verify(matchmakingService, never()).addPlayerToQueue(any(), anyDouble(), anyDouble());
        assertTrue(headerAccessor.getSessionAttributes().containsKey("disconnect"));
        assertEquals(true, headerAccessor.getSessionAttributes().get("disconnect"));
    }

    @Test
    void testRemoveFromQueue() {
        UUID playerId = UUID.randomUUID();
        String playerIdString = playerId.toString();

        soloQueueController.removeFromQueue(playerIdString);

        verify(matchmakingService).removePlayerFromQueue(playerId);
    }

    @Test
    void testAddToQueue_InvalidUUID() {
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId("invalid-uuid");
        queueRequest.setLocation(new Location(10.0, 20.0));

        soloQueueController.addToQueue(queueRequest, headerAccessor);

        verify(playerProfileRepository, never()).findByUserId(any());
        verify(matchmakingService, never()).addPlayerToQueue(any(), anyDouble(), anyDouble());
        assertTrue(headerAccessor.getSessionAttributes().containsKey("disconnect"));
    }

    @Test
    public void testAddToQueue_NullLocation() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId(playerId.toString());
        queueRequest.setLocation(null); // Set location to null

        PlayerProfile mockProfile = new PlayerProfile();
        when(playerProfileRepository.findByUserId(playerId)).thenReturn(mockProfile);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>()); // Initialize session attributes

        // Act
        soloQueueController.addToQueue(queueRequest, headerAccessor);

        // Assert
        verify(matchmakingService, never()).addPlayerToQueue(any(), anyDouble(), anyDouble());
        assertTrue((Boolean) headerAccessor.getSessionAttributes().get("disconnect"));
    }
}