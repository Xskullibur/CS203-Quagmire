package com.project.G1_T3.matchmaking.controller;

import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.UUID;

import static org.mockito.Mockito.*;

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
    }

    @Test
    void testAddToQueue() {
        UUID playerId = UUID.randomUUID();
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setUserId(playerId);

        when(playerProfileRepository.findByUserId(playerId)).thenReturn(playerProfile);

        soloQueueController.addToQueue(playerId.toString(), headerAccessor);

        verify(playerProfileRepository).findByUserId(playerId);
        verify(matchmakingService).addPlayerToQueue(playerProfile);
    }

    @Test
    void testAddToQueue_playerNotFound() {
        UUID playerId = UUID.randomUUID();

        when(playerProfileRepository.findByUserId(playerId)).thenReturn(null);

        soloQueueController.addToQueue(playerId.toString(), headerAccessor);

        verify(playerProfileRepository).findByUserId(playerId);
        verify(matchmakingService, never()).addPlayerToQueue(any());
    }

    @Test
    void testRemoveFromQueue() {
        UUID playerId = UUID.randomUUID();

        soloQueueController.removeFromQueue(playerId.toString());

        verify(matchmakingService).removePlayerFromQueue(playerId);
    }
}