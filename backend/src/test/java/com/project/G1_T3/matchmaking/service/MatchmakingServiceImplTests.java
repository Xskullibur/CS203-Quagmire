package com.project.G1_T3.matchmaking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.project.G1_T3.common.exception.InsufficientPlayersException;
import com.project.G1_T3.common.exception.MatchmakingException;
import com.project.G1_T3.common.exception.PlayerNotFoundException;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.player.model.PlayerProfile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

class MatchmakingServiceImplTests {

    @Mock
    private MatchmakingAlgorithm matchmakingAlgorithm;
    @Mock
    private MeetingPointService meetingPointService;

    private MatchmakingServiceImpl matchmakingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchmakingService = new MatchmakingServiceImpl(matchmakingAlgorithm, meetingPointService, matchService,
                messagingTemplate);
    }

    @Test
    void testAddPlayerToQueue() {
        // Arrange
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());

        // Act
        matchmakingService.addPlayerToQueue(player, 0, 0);

        // Assert
        assertTrue(matchmakingService.isPlayerInQueue(player.getUserId()));
    }

    @Test
    void testRemovePlayerFromQueue() {
        // Arrange
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());
        matchmakingService.addPlayerToQueue(player, 0, 0);

        // Act
        matchmakingService.removePlayerFromQueue(player.getUserId());

        // Assert
        assertFalse(matchmakingService.isPlayerInQueue(player.getUserId()));
    }

    @Test
    void testRemovePlayerFromQueue_PlayerNotFound() {
        // Arrange
        UUID nonExistentPlayerId = UUID.randomUUID();

        // Act & Assert
        assertThrows(PlayerNotFoundException.class, () -> {
            matchmakingService.removePlayerFromQueue(nonExistentPlayerId);
        });
    }

    @Test
    void testFindMatch_InsufficientPlayers() {
        // Act & Assert
        assertThrows(InsufficientPlayersException.class, () -> {
            matchmakingService.findMatch();
        });
    }

    @Test
    void testFindMatch_MatchFound() {
        // Arrange
        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        player1.setProfileId(UUID.randomUUID());
        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());
        player2.setProfileId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player1, 0, 0);
        matchmakingService.addPlayerToQueue(player2, 0, 0);

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(true);
        when(meetingPointService.findMeetingPoint(any(), any())).thenReturn(new double[] { 0, 0 });

        doNothing().when(messagingTemplate).convertAndSendToUser(anyString(), anyString(), any());

        // Act
        Match match = matchmakingService.findMatch();

        UUID player1ProfileId = player1.getProfileId();
        UUID player2ProfileId = player2.getProfileId();

        // Assert
        assertNotNull(match);
        assertEquals(Match.GameType.SOLO, match.getGameType());
        assertTrue(
                (player1ProfileId.equals(match.getPlayer1Id()) && player2ProfileId.equals(match.getPlayer2Id())) ||
                        (player2ProfileId.equals(match.getPlayer1Id())
                                && player1ProfileId.equals(match.getPlayer2Id())),
                "The match should contain both players, regardless of order");
        verify(messagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    void testFindMatch_NoSuitableMatch() {
        // Arrange
        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player1, 0, 0);
        matchmakingService.addPlayerToQueue(player2, 0, 0);

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(false);

        // Act & Assert
        assertThrows(MatchmakingException.class, () -> {
            matchmakingService.findMatch();
        });
    }
}