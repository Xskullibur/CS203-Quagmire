package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MatchmakingServiceTest {

    private MatchmakingService matchmakingService;

    @Mock
    private MatchmakingAlgorithm matchmakingAlgorithm;

    @Mock
    private MeetingPointService meetingPointService;

    @BeforeEach
    void setUp() {
        matchmakingService = new MatchmakingService(matchmakingAlgorithm, meetingPointService);
    }

    @Test
    void testAddPlayerToQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(UUID.randomUUID());
        player.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player, 0.0, 0.0);

        // Verify player is in queue
        Match match = matchmakingService.findMatch();
        assertNull(match); // No match yet as only one player
    }

    @Test
    void testRemovePlayerFromQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(UUID.randomUUID());
        player.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player, 0.0, 0.0);
        matchmakingService.removePlayerFromQueue(player.getProfileId());

        // Verify player is removed from queue
        Match match = matchmakingService.findMatch();
        assertNull(match); // No match as player was removed
    }

    @Test
    void testFindMatch_Success() {
        PlayerProfile player1 = new PlayerProfile();
        player1.setProfileId(UUID.randomUUID());
        player1.setUserId(UUID.randomUUID());

        PlayerProfile player2 = new PlayerProfile();
        player2.setProfileId(UUID.randomUUID());
        player2.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player1, 0.0, 0.0);
        matchmakingService.addPlayerToQueue(player2, 0.0, 0.0);

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(true);

        Match match = matchmakingService.findMatch();

        assertNotNull(match);
        assertEquals(Match.GameType.SOLO, match.getGameType());
        assertEquals(Match.MatchStatus.SCHEDULED, match.getStatus());
        assertTrue(match.getPlayer1Id().equals(player1.getProfileId())
                || match.getPlayer1Id().equals(player2.getProfileId()));
        assertTrue(match.getPlayer2Id().equals(player1.getProfileId())
                || match.getPlayer2Id().equals(player2.getProfileId()));
        assertNotEquals(match.getPlayer1Id(), match.getPlayer2Id());
    }

    @Test
    void testFindMatch_NoMatch() {
        PlayerProfile player1 = new PlayerProfile();
        player1.setProfileId(UUID.randomUUID());
        player1.setUserId(UUID.randomUUID());

        PlayerProfile player2 = new PlayerProfile();
        player2.setProfileId(UUID.randomUUID());
        player2.setUserId(UUID.randomUUID());

        matchmakingService.addPlayerToQueue(player1, 0.0, 0.0);
        matchmakingService.addPlayerToQueue(player2, 0.0, 0.0);

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(false);

        Match match = matchmakingService.findMatch();

        assertNull(match);
    }
}