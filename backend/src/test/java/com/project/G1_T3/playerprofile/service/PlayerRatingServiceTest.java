package com.project.G1_T3.playerprofile.service;

import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PlayerRatingServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private PlayerRatingService playerRatingService;

    @BeforeEach
    public void setUp() {
        // Prepare a list of mock PlayerProfiles with different ratings
        List<PlayerProfile> mockPlayers = new ArrayList<>();
        
        // Add 10 players with rating 1500
        for (int i = 0; i < 10; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setGlickoRating(1500);
            mockPlayers.add(player);
        }

        // Add 5 players with rating 1501
        for (int i = 0; i < 5; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setGlickoRating(1501);
            mockPlayers.add(player);
        }

        // Add 8 players with rating 1499
        for (int i = 0; i < 8; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setGlickoRating(1499);
            mockPlayers.add(player);
        }

        // Mock the repository call to return this list of players
        when(playerProfileRepository.findAll()).thenReturn(mockPlayers);

        // Initialize the rating buckets and prefix sums
        playerRatingService.initializeBuckets();
    }

    @Test
    void testInitialBucketCounts() {
        // Test that the bucketCounts are initialized correctly
        int[] bucketCounts = playerRatingService.getBucketCounts();
        assertEquals(10, bucketCounts[1500], "Bucket for rating 1500 should have 10 players");
        assertEquals(5, bucketCounts[1501], "Bucket for rating 1501 should have 5 players");
        assertEquals(8, bucketCounts[1499], "Bucket for rating 1499 should have 8 players");
    }

    @Test
    void testPrefixSums() {
        // Test that prefix sums are calculated correctly
        int numberOfPlayersAhead1500 = playerRatingService.getNumberOfPlayersAhead(1500);
        assertEquals(5, numberOfPlayersAhead1500, "Number of players ahead of rating 1500 should be 5");

        int numberOfPlayersAhead1499 = playerRatingService.getNumberOfPlayersAhead(1499);
        assertEquals(15, numberOfPlayersAhead1499, "Number of players ahead of rating 1499 should be 15");
    }

    @Test
    void testUpdateRating() {
        // Retrieve a player's ID with a rating of 1500 for the update
        Set<UUID> playerIds = playerRatingService.getPlayersInBucket(1500);
        UUID playerId = null;
        for(UUID pId : playerIds){
            playerId = pId;
            break;
        }

        // Update the player's rating from 1500 to 1502
        playerRatingService.updateRating(playerId, 1500, 1502);

        // Verify bucket counts
        int[] bucketCounts = playerRatingService.getBucketCounts();
        assertEquals(9, bucketCounts[1500], "Bucket for rating 1500 should have 9 players after update");
        assertEquals(1, bucketCounts[1502], "Bucket for rating 1502 should have 1 player after update");

        // Verify prefix sums
        int numberOfPlayersAhead1500 = playerRatingService.getNumberOfPlayersAhead(1500);
        assertEquals(6, numberOfPlayersAhead1500, "Number of players ahead of rating 1500 should be 6");
    }

    @Test
    void testAddPlayer() {
        // Create a new player with a rating of 1500 and add them to the service
        UUID playerId = UUID.randomUUID();
        playerRatingService.addPlayer(playerId, 1500);

        // Verify that the bucket count for rating 1500 increased
        assertEquals(11, playerRatingService.getBucketCounts()[1500],
                "The bucket count for the rating should be incremented.");

        // Verify prefix sums are correctly updated
        int numberOfPlayersAhead1500 = playerRatingService.getNumberOfPlayersAhead(1500);
        int numberOfPlayersAhead1499 = playerRatingService.getNumberOfPlayersAhead(1499);
        int totalPlayers = playerRatingService.getTotalPlayers();

        assertEquals(5, numberOfPlayersAhead1500, "The number of players higher than initial rating should remain the same");
        assertEquals(16, numberOfPlayersAhead1499, "Number of players ahead 1499 should be one more now");
        assertEquals(24, totalPlayers, "Total players should have increased");
    }

    @Test
    void testCalculationOfRankingWhenBest() {
        int playerRating = 1501;
        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(playerRating);
        int numberOfPlayersInBucket = playerRatingService.getNumberOfPlayersInBucket(playerRating);
        int totalPlayers = playerRatingService.getTotalPlayers();

        // Rank percentage calculation
        double rankPercentage = ((double) (numberOfPlayersAhead + numberOfPlayersInBucket)) / totalPlayers * 100;
        assertEquals(5.0 / 23 * 100, rankPercentage, 0.01);
    }

    @Test
    void testCalculationOfRankingWhenWorst() {
        int playerRating = 1499;
        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(playerRating);
        int numberOfPlayersInBucket = playerRatingService.getNumberOfPlayersInBucket(playerRating);
        int totalPlayers = playerRatingService.getTotalPlayers();

        double rankPercentage = ((double) (numberOfPlayersAhead + numberOfPlayersInBucket)) / totalPlayers * 100;

        assertEquals(8, numberOfPlayersInBucket);
        assertEquals(23, totalPlayers);
        assertEquals(15, numberOfPlayersAhead);
        assertEquals(100, rankPercentage, 0.01);
    }

    @Test
    void testCalculationOfRankingWhenDominating() {
        int playerRating = 1800;
        UUID playerId = UUID.randomUUID();
        playerRatingService.addPlayer(playerId, playerRating);

        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(playerRating);
        int numberOfPlayersInBucket = playerRatingService.getNumberOfPlayersInBucket(playerRating);
        int totalPlayers = playerRatingService.getTotalPlayers();

        double rankPercentage = ((double) (numberOfPlayersAhead + numberOfPlayersInBucket)) / totalPlayers * 100;
        assertEquals(1.0 / 24 * 100, rankPercentage, 0.01);
    }
}
