package com.project.G1_T3.matchmaking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.G1_T3.common.exception.PlayerAlreadyInQueueException;
import com.project.G1_T3.common.exception.PlayerNotFoundException;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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

    private PlayerQueue playerQueue;

    @Mock
    private LocationService locationService;

    @Mock
    private GlickoMatchmaking glickoMatchmaking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MatchmakingKDTree kdTree = new MatchmakingKDTree();
        playerQueue = new PlayerQueueImpl(kdTree, glickoMatchmaking);

        // Inject the mocked GlickoMatchmaking into PlayerQueueImpl
        glickoMatchmaking = mock(GlickoMatchmaking.class);
        ((PlayerQueueImpl) playerQueue).glickoMatchmaking = glickoMatchmaking;

        // Set up GlickoMatchmaking methods
        when(glickoMatchmaking.getMaxRatingDiff()).thenReturn(1000.0);
        when(glickoMatchmaking.getMaxDeviationDiff()).thenReturn(1000.0);
        when(glickoMatchmaking.getMaxDistanceKm()).thenReturn(1000.0);
        when(glickoMatchmaking.isGoodMatch(any(), any())).thenReturn(true);

        matchmakingService = new MatchmakingServiceImpl(meetingPointService, matchService,
            messagingTemplate, playerProfileService, playerQueue);
    }

    @Test
    void testAddPlayerToQueue() {
        PlayerProfile player = new PlayerProfile();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        player.setUser(user);

        matchmakingService.addPlayerToQueue(player, 0, 0);

        assertTrue(matchmakingService.isPlayerInQueue(player.getUser().getId()));
    }

    @Test
    void testRemovePlayerFromQueue() {
        PlayerProfile player = new PlayerProfile();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        player.setUser(user);
        matchmakingService.addPlayerToQueue(player, 0, 0);

        matchmakingService.removePlayerFromQueue(player.getUser().getId());

        assertFalse(matchmakingService.isPlayerInQueue(player.getUser().getId()));
    }

    @Test
    void testRemovePlayerFromQueue_PlayerNotFound() {
        UUID nonExistentPlayerId = UUID.randomUUID();

        assertThrows(PlayerNotFoundException.class, () -> {
            matchmakingService.removePlayerFromQueue(nonExistentPlayerId);
        });
    }

    @Test
    void testAddingSamePlayerTwice() {
        PlayerProfile player = new PlayerProfile();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        player.setUser(user);

        matchmakingService.addPlayerToQueue(player, 0, 0);

        assertThrows(PlayerAlreadyInQueueException.class, () -> {
            matchmakingService.addPlayerToQueue(player, 0, 0);
        });
    }

    @Test
    void testFindMatch_MatchFound() {
        PlayerProfile player1 = new PlayerProfile();
        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        player1.setUser(user1);
        player1.setProfileId(UUID.randomUUID());
        player1.setGlickoRating(1500);
        player1.setRatingDeviation(200);

        PlayerProfile player2 = new PlayerProfile();
        User user2 = mock(User.class);
        user2.setUserId(UUID.randomUUID());
        player2.setUser(user2);
        player2.setProfileId(UUID.randomUUID());
        player2.setGlickoRating(1500);
        player2.setRatingDeviation(200);

        matchmakingService.addPlayerToQueue(player1, 0, 0);
        matchmakingService.addPlayerToQueue(player2, 0, 0);

        // Ensure that matchmakingAlgorithm.isGoodMatch returns true
        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(true);
        // Ensure that glickoMatchmaking.isGoodMatch returns true
        when(glickoMatchmaking.isGoodMatch(any(), any())).thenReturn(true);

        when(meetingPointService.findMeetingPoint(any(), any())).thenReturn(new double[]{0, 0});

        Match mockMatch = new Match();
        mockMatch.setMatchId(UUID.randomUUID());
        mockMatch.setPlayer1Id(player1.getProfileId());
        mockMatch.setPlayer2Id(player2.getProfileId());
        mockMatch.setGameType(Match.GameType.SOLO);
        when(matchService.createMatch(any(MatchDTO.class))).thenReturn(mockMatch);

        when(playerProfileService.findByProfileId(anyString())).thenReturn(mockOpponentProfile);
        when(mockOpponentProfile.getName()).thenReturn("MockOpponent");

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
}