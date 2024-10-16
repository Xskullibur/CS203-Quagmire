package com.project.G1_T3.matchmaking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

class EloMatchmakingAlgorithmTests {

    private EloMatchmakingAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new EloMatchmakingAlgorithm();
    }

    @Test
    void testIsGoodMatch_WithinRatingAndDistance() {
        // Arrange
        PlayerProfile profile1 = createPlayerProfile(1000f);
        PlayerProfile profile2 = createPlayerProfile(1100f);

        QueuedPlayer player1 = new QueuedPlayer(profile1, 0, 0);
        QueuedPlayer player2 = new QueuedPlayer(profile2, 0.1, 0.1);

        // Act
        boolean isGoodMatch = algorithm.isGoodMatch(player1, player2);

        // Assert
        assertTrue(isGoodMatch);
    }

    @Test
    void testIsGoodMatch_OutsideRatingRange() {
        // Arrange
        PlayerProfile profile1 = createPlayerProfile(1000f);
        PlayerProfile profile2 = createPlayerProfile(1500f);

        QueuedPlayer player1 = new QueuedPlayer(profile1, 0, 0);
        QueuedPlayer player2 = new QueuedPlayer(profile2, 0.1, 0.1);

        // Act
        boolean isGoodMatch = algorithm.isGoodMatch(player1, player2);

        // Assert
        assertFalse(isGoodMatch);
    }

    @Test
    void testIsGoodMatch_OutsideDistanceRange() {
        // Arrange
        PlayerProfile profile1 = createPlayerProfile(1000f);
        PlayerProfile profile2 = createPlayerProfile(1100f);

        QueuedPlayer player1 = new QueuedPlayer(profile1, 0, 0);
        QueuedPlayer player2 = new QueuedPlayer(profile2, 1, 1); // Approx. 157 km apart

        // Act
        boolean isGoodMatch = algorithm.isGoodMatch(player1, player2);

        // Assert
        assertFalse(isGoodMatch);
    }

    @Test
    void testIsGoodMatch_LongQueueTime() {
        // Arrange
        PlayerProfile profile1 = createPlayerProfile(1000f);
        PlayerProfile profile2 = createPlayerProfile(1500f);

        // Simulate long queue time by creating QueuedPlayer with a past join time
        Instant pastTime = Instant.now().minusSeconds(301); // 5 minutes and 1 second ago
        QueuedPlayer player1 = new QueuedPlayer(profile1, 0, 0) {
            @Override
            public Instant getJoinTime() {
                return pastTime;
            }
        };
        QueuedPlayer player2 = new QueuedPlayer(profile2, 1, 1);

        // Act
        boolean isGoodMatch = algorithm.isGoodMatch(player1, player2);

        // Assert
        assertFalse(isGoodMatch);
    }

    private PlayerProfile createPlayerProfile(float rating) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUserId(UUID.randomUUID());
        profile.setCurrentRating(rating);
        return profile;
    }
}