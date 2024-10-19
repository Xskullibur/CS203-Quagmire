package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.common.glicko.Glicko2Result;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlayerProfileServiceIntegrationTest {

    @Autowired
    private PlayerProfileService playerProfileService;

    @Autowired
    private PlayerRatingService playerRatingService;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Test
    public void testRatingUpdateAndPositionFinding() {
        // Arrange
        // Create and save player profiles
        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        player1.setGlickoRating(1500);
        playerProfileRepository.save(player1);

        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());
        player2.setGlickoRating(1500);
        playerProfileRepository.save(player2);

        // Initialize buckets
        playerRatingService.initializeBuckets();

        // Act
        // Simulate a match where player1 wins against player2
        Glicko2Result result = new Glicko2Result(player2.getGlickoRating(), player2.getRatingDeviation(), 1.0);
        List<Glicko2Result> results = Collections.singletonList(result);

        playerProfileService.updatePlayerRating(player1.getProfileId(), results);

        // Fetch updated profiles
        PlayerProfile updatedPlayer1 = playerProfileService.findByProfileId(player1.getProfileId().toString());
        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(updatedPlayer1.getGlickoRating());

        // Assert
        assertTrue(updatedPlayer1.getGlickoRating() > 1500, "Player1's rating should have increased");
        assertEquals(numberOfPlayersAhead, playerRatingService.getNumberOfPlayersAhead(updatedPlayer1.getGlickoRating()), "Number of players ahead should be accurate");
    }

    @Test
    public void testConcurrentRatingUpdates() throws InterruptedException {
        // Arrange
        int numThreads = 10;
        List<Thread> threads = new ArrayList<>();
        UUID playerId = UUID.randomUUID();

        PlayerProfile player = new PlayerProfile();
        player.setUserId(playerId);
        player.setGlickoRating(1500);
        playerProfileRepository.save(player);

        // Initialize buckets
        playerRatingService.initializeBuckets();

        // Act
        // Simulate concurrent rating updates
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                Glicko2Result result = new Glicko2Result(1500, 200, 1.0);
                List<Glicko2Result> results = Collections.singletonList(result);
                playerProfileService.updatePlayerRating(player.getProfileId(), results);
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        // Fetch updated profile
        PlayerProfile updatedPlayer = playerProfileService.findByProfileId(player.getProfileId().toString());
        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(updatedPlayer.getGlickoRating());

        // Assert
        assertNotNull(updatedPlayer);
        assertTrue(updatedPlayer.getGlickoRating() > 1500, "Player's rating should have increased after multiple updates");
        assertEquals(numberOfPlayersAhead, playerRatingService.getNumberOfPlayersAhead(updatedPlayer.getGlickoRating()), "Number of players ahead should be accurate");
    }
}
