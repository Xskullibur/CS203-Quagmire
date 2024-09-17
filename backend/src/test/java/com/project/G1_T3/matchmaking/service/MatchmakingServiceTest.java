package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.player.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MatchmakingServiceTest {

    private MatchmakingService matchmakingService;

    @BeforeEach
    void setUp() {
        matchmakingService = new MatchmakingService();
    }

    @Test
    void testAddPlayerToQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());
        
        matchmakingService.addPlayerToQueue(player);
        
        // Since the queue is private, we can't directly check it.
        // Instead, we'll verify that calling findMatch doesn't return a match yet.
        assertNull(matchmakingService.findMatch());
    }

    @Test
    void testRemovePlayerFromQueue() {
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());
        
        matchmakingService.addPlayerToQueue(player);
        matchmakingService.removePlayerFromQueue(player.getUserId());
        
        // Verify that the queue is empty by trying to find a match
        assertNull(matchmakingService.findMatch());
    }

    @Test
    void testFindMatch_withTwoPlayers() {
        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        
        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());
        
        matchmakingService.addPlayerToQueue(player1);
        matchmakingService.addPlayerToQueue(player2);
        
        Match match = matchmakingService.findMatch();
        
        assertNotNull(match);
        assertEquals(Match.GameType.SOLO, match.getGameType());
        assertEquals(Match.MatchStatus.SCHEDULED, match.getStatus());
        assertTrue(match.getPlayer1Id().equals(player1.getUserId()) || match.getPlayer1Id().equals(player2.getUserId()));
        assertTrue(match.getPlayer2Id().equals(player1.getUserId()) || match.getPlayer2Id().equals(player2.getUserId()));
        assertNotEquals(match.getPlayer1Id(), match.getPlayer2Id());
    }

    @Test
    void testFindMatch_withOnePlayer() {
        PlayerProfile player = new PlayerProfile();
        player.setUserId(UUID.randomUUID());
        
        matchmakingService.addPlayerToQueue(player);
        
        Match match = matchmakingService.findMatch();
        
        assertNull(match);
    }
}