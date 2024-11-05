package com.project.G1_T3.matchmaking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;

@ExtendWith(MockitoExtension.class)
class GlickoMatchmakingTest {
    @Mock
    private LocationService locationService;

    private GlickoMatchmaking matchmaking;

    @BeforeEach
    void setUp() {
        matchmaking = new GlickoMatchmaking(locationService);
    }

    @Test
    void testIsGoodMatch_WithinThresholds() {
        // Create two players with similar ratings
        PlayerProfile player1Profile = new PlayerProfile();
        player1Profile.setGlickoRating(1500);
        player1Profile.setRatingDeviation(50);

        PlayerProfile player2Profile = new PlayerProfile();
        player2Profile.setGlickoRating(1600);
        player2Profile.setRatingDeviation(60);

        QueuedPlayer player1 = new QueuedPlayer(player1Profile, 0.0, 0.0);
        QueuedPlayer player2 = new QueuedPlayer(player2Profile, 0.0, 0.001);

        when(locationService.calculateDistance(eq(0.0), eq(0.0), eq(0.0), eq(0.001)))
                .thenReturn(1.0); // 1km distance

        assertTrue(matchmaking.isGoodMatch(player1, player2));
        verify(locationService).calculateDistance(0.0, 0.0, 0.0, 0.001);
    }

    @Test
    void testIsGoodMatch_OutsideThresholds() {
        PlayerProfile player1Profile = new PlayerProfile();
        player1Profile.setGlickoRating(1500);
        player1Profile.setRatingDeviation(50);

        PlayerProfile player2Profile = new PlayerProfile();
        player2Profile.setGlickoRating(2000); // Too high rating difference
        player2Profile.setRatingDeviation(200); // Too high deviation difference

        QueuedPlayer player1 = new QueuedPlayer(player1Profile, 0.0, 0.0);
        QueuedPlayer player2 = new QueuedPlayer(player2Profile, 1.0, 1.0);

        when(locationService.calculateDistance(eq(0.0), eq(0.0), eq(1.0), eq(1.0)))
                .thenReturn(5.0); // 5km distance

        assertFalse(matchmaking.isGoodMatch(player1, player2));
        verify(locationService).calculateDistance(0.0, 0.0, 1.0, 1.0);
    }

    @Test
    void testIsGoodMatch_NullPlayers() {
        assertThrows(NullPointerException.class, () -> matchmaking.isGoodMatch(null, null));
    }

    @Test
    void testIsGoodMatch_OneNullPlayer() {
        PlayerProfile profile = new PlayerProfile();
        profile.setGlickoRating(1500);
        profile.setRatingDeviation(50);
        QueuedPlayer player = new QueuedPlayer(profile, 0.0, 0.0);

        assertThrows(NullPointerException.class, () -> matchmaking.isGoodMatch(player, null));
        assertThrows(NullPointerException.class, () -> matchmaking.isGoodMatch(null, player));
    }
}