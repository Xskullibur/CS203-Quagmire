package com.project.G1_T3.matchmaking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.project.G1_T3.common.exception.PlayerAlreadyInQueueException;
import com.project.G1_T3.common.exception.PlayerNotFoundException;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
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
        // Set up player 1
        PlayerProfile player1 = new PlayerProfile();
        User user1 = new User();
        UUID player1UserId = UUID.randomUUID();
        UUID player1ProfileId = UUID.randomUUID();
        user1.setUserId(player1UserId);
        player1.setUser(user1);
        player1.setProfileId(player1ProfileId);
        player1.setGlickoRating(1500);
        player1.setRatingDeviation(200);

        // Set up player 2
        PlayerProfile player2 = new PlayerProfile();
        User user2 = new User();
        UUID player2UserId = UUID.randomUUID();
        UUID player2ProfileId = UUID.randomUUID();
        user2.setUserId(player2UserId);
        player2.setUser(user2);
        player2.setProfileId(player2ProfileId);
        player2.setGlickoRating(1500);
        player2.setRatingDeviation(200);

        // Add players to queue
        matchmakingService.addPlayerToQueue(player1, 0, 0);
        matchmakingService.addPlayerToQueue(player2, 0, 0);

        // Mock meeting point service
        double[] meetingPoint = new double[] { 42.3601, -71.0589 }; // Example coordinates
        when(meetingPointService.findMeetingPoint(any(), any())).thenReturn(meetingPoint);

        // Create and configure mock match
        Match mockMatch = new Match();
        mockMatch.setMatchId(UUID.randomUUID());
        mockMatch.setPlayer1Id(player1ProfileId);
        mockMatch.setPlayer2Id(player2ProfileId);
        mockMatch.setGameType(Match.GameType.SOLO);
        mockMatch.setMeetingLatitude(meetingPoint[0]); // Set the latitude
        mockMatch.setMeetingLongitude(meetingPoint[1]); // Set the longitude

        // Mock match creation
        when(matchService.createMatch(any(MatchDTO.class))).thenReturn(mockMatch);

        // Mock opponent profile retrieval
        when(playerProfileService.findByProfileId(any(UUID.class))).thenReturn(mockOpponentProfile);
        when(mockOpponentProfile.getName()).thenReturn("MockOpponent");

        // Mock messaging with specific argument matchers
        doNothing().when(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + player1ProfileId),
                any(MatchNotification.class));
        doNothing().when(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + player2ProfileId),
                any(MatchNotification.class));

        // Execute test
        Match match = matchmakingService.findMatch();

        // Verify results
        assertNotNull(match);
        assertEquals(Match.GameType.SOLO, match.getGameType());
        assertTrue(
                (player1ProfileId.equals(match.getPlayer1Id()) && player2ProfileId.equals(match.getPlayer2Id())) ||
                        (player2ProfileId.equals(match.getPlayer1Id())
                                && player1ProfileId.equals(match.getPlayer2Id())),
                "The match should contain both players, regardless of order");
        assertEquals(meetingPoint[0], match.getMeetingLatitude());
        assertEquals(meetingPoint[1], match.getMeetingLongitude());

        // Verify notifications were sent with specific destinations
        verify(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + player1ProfileId),
                any(MatchNotification.class));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + player2ProfileId),
                any(MatchNotification.class));
        verify(playerProfileService, times(2)).findByProfileId(any(UUID.class));
    }
}
