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
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;

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
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private MatchService matchService;

    @Mock
    private PlayerProfileService playerProfileService;

    private MatchmakingServiceImpl matchmakingService;

    @Mock
    private PlayerProfile mockOpponentProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchmakingService = new MatchmakingServiceImpl(matchmakingAlgorithm, meetingPointService, matchService,
                messagingTemplate, playerProfileService);
    }

    @Test
    void testAddPlayerToQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player, 0, 0);

        assertTrue(matchmakingService.isPlayerInQueue(player.getUserId()));
    }

    @Test
    void testRemovePlayerFromQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());
        matchmakingService.addPlayerToQueue(player, 0, 0);

        matchmakingService.removePlayerFromQueue(player.getUserId());

        assertFalse(matchmakingService.isPlayerInQueue(player.getUserId()));
    }

    @Test
    void testRemovePlayerFromQueue_PlayerNotFound() {
        UUID nonExistentPlayerId = UUID.randomUUID();

        assertThrows(PlayerNotFoundException.class, () -> {
            matchmakingService.removePlayerFromQueue(nonExistentPlayerId);
        });
    }

    @Test
    void testFindMatch_MatchFound() {
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

        Match mockMatch = new Match();
        mockMatch.setMatchId(UUID.randomUUID());
        mockMatch.setPlayer1Id(player1.getProfileId());
        mockMatch.setPlayer2Id(player2.getProfileId());
        mockMatch.setGameType(Match.GameType.SOLO);
        when(matchService.createMatch(any(MatchDTO.class))).thenReturn(mockMatch);

        when(playerProfileService.findByProfileId(anyString())).thenReturn(mockOpponentProfile);
        when(mockOpponentProfile.getUsername()).thenReturn("MockOpponent");

        doNothing().when(messagingTemplate).convertAndSend(any(String.class), any(Object.class));

        Match match = matchmakingService.findMatch();

        assertNotNull(match);
        assertEquals(Match.GameType.SOLO, match.getGameType());
        assertTrue(
                (player1.getProfileId().equals(match.getPlayer1Id())
                        && player2.getProfileId().equals(match.getPlayer2Id())) ||
                        (player2.getProfileId().equals(match.getPlayer1Id())
                                && player1.getProfileId().equals(match.getPlayer2Id())),
                "The match should contain both players, regardless of order");

        verify(messagingTemplate, times(2)).convertAndSend(any(String.class), any(Object.class));
        verify(playerProfileService, times(2)).findByProfileId(anyString());
    }

    @Test
    void testFindMatch_NoSuitableMatch() {
        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player1, 0, 0);
        matchmakingService.addPlayerToQueue(player2, 0, 0);

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(false);

        assertThrows(MatchmakingException.class, () -> {
            matchmakingService.findMatch();
        });
    }
}