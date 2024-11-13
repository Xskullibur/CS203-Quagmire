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
            player.setProfileId(UUID.randomUUID()); // Assign a unique UUID
            player.setGlickoRating(1500);
            mockPlayers.add(player);
        }

        // Add 5 players with rating 1501
        for (int i = 0; i < 5; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setProfileId(UUID.randomUUID());
            player.setGlickoRating(1501);
            mockPlayers.add(player);
        }

        // Add 8 players with rating 1499
        for (int i = 0; i < 8; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setProfileId(UUID.randomUUID());
            player.setGlickoRating(1499);
            mockPlayers.add(player);
        }

        // Mock the repository call to return this list of players
        when(playerProfileRepository.findAll()).thenReturn(mockPlayers);

        // Initialize the rating buckets and prefix sums
        playerRatingService.initializeBuckets();
    }

    @Test
    void testInitialBucketCountsAndContents() {
        // Test that the bucketCounts are initialized correctly
        int[] bucketCounts = playerRatingService.getBucketCounts();
        assertEquals(10, bucketCounts[1500], "Bucket for rating 1500 should have 10 players");
        assertEquals(5, bucketCounts[1501], "Bucket for rating 1501 should have 5 players");
        assertEquals(8, bucketCounts[1499], "Bucket for rating 1499 should have 8 players");

        // Verify that the rating buckets themselves contain the correct number of UUIDs
        Set<UUID>[] ratingBuckets = playerRatingService.getRatingBuckets();

        assertEquals(10, ratingBuckets[1500].size(), "Bucket for rating 1500 should contain 10 UUIDs");
        assertEquals(5, ratingBuckets[1501].size(), "Bucket for rating 1501 should contain 5 UUIDs");
        assertEquals(8, ratingBuckets[1499].size(), "Bucket for rating 1499 should contain 8 UUIDs");

        // Further validate that each bucket contains unique UUIDs (no duplicates within
        // buckets)
        assertEquals(10, new HashSet<>(ratingBuckets[1500]).size(),
                "Bucket for rating 1500 should contain unique UUIDs");
        assertEquals(5, new HashSet<>(ratingBuckets[1501]).size(),
                "Bucket for rating 1501 should contain unique UUIDs");
        assertEquals(8, new HashSet<>(ratingBuckets[1499]).size(),
                "Bucket for rating 1499 should contain unique UUIDs");
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
        for (UUID pId : playerIds) {
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

        assertEquals(5, numberOfPlayersAhead1500,
                "The number of players higher than initial rating should remain the same");
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

    @Test
    void testGetTop10Players() {
        // Prepare a list of player IDs with different ratings
        List<UUID> expectedTop10Players = new ArrayList<>();

        // Add 5 players with a high rating of 2000 (they should be included in the top
        // 10)
        for (int i = 0; i < 5; i++) {
            UUID playerId = UUID.randomUUID();
            playerRatingService.addPlayer(playerId, 2000);
            expectedTop10Players.add(playerId); // Keep track of these players for validation
        }

        // Add 3 players with a rating of 1999 (should be in the top 10)
        for (int i = 0; i < 3; i++) {
            UUID playerId = UUID.randomUUID();
            playerRatingService.addPlayer(playerId, 1999);
            expectedTop10Players.add(playerId);
        }

        // Add 2 players with a rating of 1998 (should also be in the top 10)
        for (int i = 0; i < 2; i++) {
            UUID playerId = UUID.randomUUID();
            playerRatingService.addPlayer(playerId, 1998);
            expectedTop10Players.add(playerId);
        }

        // Add 10 additional players with lower ratings to ensure they are not in the
        // top 10
        for (int i = 0; i < 10; i++) {
            UUID playerId = UUID.randomUUID();
            playerRatingService.addPlayer(playerId, 1500);
        }

        // Retrieve the top 10 players
        List<UUID> top10Players = playerRatingService.getTop10Players();

        // Verify that we have exactly 10 players in the list
        assertEquals(10, top10Players.size(), "Top 10 players list should contain exactly 10 players");

        // Verify that the top 10 players are the ones with the highest ratings
        assertTrue(top10Players.containsAll(expectedTop10Players),
                "Top 10 players should include those with the highest ratings");

        // Verify that the order is correct (highest ratings first)
        // Expected order: players with rating 2000, then 1999, then 1998
        List<Integer> expectedRatings = Arrays.asList(2000, 2000, 2000, 2000, 2000, 1999, 1999, 1999, 1998, 1998);
        List<Integer> actualRatings = new ArrayList<>();
        for (UUID playerId : top10Players) {
            for (int rating = 3000; rating >= 0; rating--) {
                if (playerRatingService.getPlayersInBucket(rating).contains(playerId)) {
                    actualRatings.add(rating);
                    break;
                }
            }
        }
        assertEquals(expectedRatings, actualRatings, "Top 10 players should be ordered by highest ratings");
    }

    @Test
    void testTop10EdgeCaseEquallyRatedPlayers() {
        // Prepare a list of player IDs with different ratings
        List<UUID> top10List = playerRatingService.getTop10Players();
        assertTrue(top10List.size() > 10);
    }

    @Test
    void top10EdgeCaseFloatingPointDifference() {

        // Arrange
        List<UUID> expectedRetrievals = new ArrayList<>();
        List<PlayerProfile> mockRepo = new ArrayList<>();

        // Add 5 players with a high rating of 2000 (they should be included in the top
        // 10)
        for (int i = 0; i < 5; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setProfileId(UUID.randomUUID());
            player.setGlickoRating(2000 + (1 - i) * 0.01);
            mockRepo.add(player);
            playerRatingService.addPlayer(player.getProfileId(), Math.round(player.getGlickoRating()));
            expectedRetrievals.add(player.getProfileId()); // Keep track of these players for validation
        }

        // Add 10 players with a rating of 1999 (should be in the top 10)
        for (int i = 0; i < 10; i++) {
            PlayerProfile player = new PlayerProfile();
            player.setProfileId(UUID.randomUUID());
            player.setGlickoRating(1999 + (10 - i - 1) * 0.01);
            playerRatingService.addPlayer(player.getProfileId(), Math.round(player.getGlickoRating()));
            mockRepo.add(player);
            expectedRetrievals.add(player.getProfileId()); // Keep track of these players for validation
        }

        // Expected Top 10 list limited to 10 players
        List<UUID> expectedTop10 = expectedRetrievals.stream().limit(10).toList();

        // Act
        List<UUID> actualRetrievals = playerRatingService.getTop10Players();
        List<PlayerProfile> actualTop10Profiles = new ArrayList<>();

        // Get profiles from mock repo based on UUIDs in actualRetrievals
        for (PlayerProfile pfp : mockRepo) {
            for (UUID pId : actualRetrievals) {
                if (pfp.getProfileId().equals(pId)) {
                    actualTop10Profiles.add(pfp);
                }
            }
        }

        // Sort profiles by descending Glicko rating
        actualTop10Profiles.sort(
                (PlayerProfile o1, PlayerProfile o2) -> -Double.compare(o1.getGlickoRating(), o2.getGlickoRating()));

        // Convert actual top 10 profiles to a list of UUIDs
        List<UUID> actualTop10 = actualTop10Profiles.stream().limit(10).map(PlayerProfile::getProfileId).toList();

        // Assert Top 10 retrieval
        assertEquals(Set.of(expectedRetrievals.toArray()), Set.of(actualRetrievals.toArray()));
        assertEquals(expectedTop10, actualTop10, "The top 10 UUIDs should match the expected top 10 order");

        // Additional assertions for bucket counts

        // Verify number of players in bucket 2000
        int bucket2000Count = playerRatingService.getRatingBuckets()[2000].size();
        assertEquals(5, bucket2000Count, "Bucket for rating 2000 should contain 5 players");

        // Verify number of players in bucket 1999
        int bucket1999Count = playerRatingService.getRatingBuckets()[1999].size();
        assertEquals(10, bucket1999Count, "Bucket for rating 1999 should contain 10 players");

        // Verify that the players in each bucket are correctly assigned based on
        // rounded ratings
        Set<UUID> bucket2000Players = playerRatingService.getRatingBuckets()[2000];
        Set<UUID> bucket1999Players = playerRatingService.getRatingBuckets()[1999];

        List<UUID> actual2000BucketUUIDs = mockRepo.stream()
                .filter(player -> Math.round(player.getGlickoRating()) == 2000)
                .map(PlayerProfile::getProfileId)
                .toList();
        List<UUID> actual1999BucketUUIDs = mockRepo.stream()
                .filter(player -> Math.round(player.getGlickoRating()) == 1999)
                .map(PlayerProfile::getProfileId)
                .toList();

        assertTrue(bucket2000Players.containsAll(actual2000BucketUUIDs),
                "Bucket for rating 2000 should contain the correct players");
        assertTrue(bucket1999Players.containsAll(actual1999BucketUUIDs),
                "Bucket for rating 1999 should contain the correct players");

        // Verify order of Top 10 based on actual Glicko rating
        List<Float> expectedRatings = actualTop10Profiles.stream()
                .map(PlayerProfile::getGlickoRating)
                .sorted(Comparator.reverseOrder())
                .toList();
        List<Float> actualRatings = actualTop10Profiles.stream()
                .map(PlayerProfile::getGlickoRating)
                .toList();
        assertEquals(expectedRatings, actualRatings,
                "Top 10 players should be ordered by their Glicko ratings in descending order");
    }

}
