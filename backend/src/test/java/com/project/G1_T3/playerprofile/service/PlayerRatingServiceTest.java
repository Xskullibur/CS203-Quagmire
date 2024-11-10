package com.project.G1_T3.playerprofile.service;

import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerRatingServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private PlayerRatingService playerRatingService;

    @BeforeEach
    public void setUp() {
        // Mock the getRatingCounts() method
        List<Object[]> mockCounts = new ArrayList<>();
        mockCounts.add(new Object[] { 1500, 10L });
        mockCounts.add(new Object[] { 1501, 5L });
        mockCounts.add(new Object[] { 1499, 8L });

        when(playerProfileRepository.getRatingCounts()).thenReturn(mockCounts);

        // Initialize the buckets
        playerRatingService.initializeBuckets();
    }

    @Test
    void testInitialBucketCounts() {
        // Test that the bucketCounts are initialized correctly
        int[] bucketCounts = playerRatingService.getBucketCounts(); // You'll need to add a getter or make the field
                                                                    // package-private for testing
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
        // Update a player's rating from 1500 to 1502
        playerRatingService.updateRating(1500, 1502);

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
        // act
        int initialRating = 1500;
        playerRatingService.addPlayer(initialRating);

        // assert
        assertEquals(11, playerRatingService.getBucketCounts()[initialRating],
                "The bucket count for the rating should be incremented.");

        // Verify prefix sums are correctly updated
        int numberOfPlayersAhead1500 = playerRatingService.getNumberOfPlayersAhead(initialRating);
        int numberOfPlayersAhead1501 = playerRatingService.getNumberOfPlayersAhead(initialRating - 1);
        int totalPlayers = playerRatingService.getTotalPlayers();

        assertEquals(5, numberOfPlayersAhead1500,
                "The number of players higher than inital rating should remain the same");
        assertEquals(16, numberOfPlayersAhead1501, "number of players ahead 1501 should be one more now");
        assertEquals(24, totalPlayers, "total players should have increased");
    }

}
