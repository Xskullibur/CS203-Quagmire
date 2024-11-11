// package com.project.G1_T3.playerprofile.controller;

// import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
// import com.project.G1_T3.playerprofile.service.PlayerProfileService;
// import com.project.G1_T3.playerprofile.service.PlayerRatingService;
// import com.project.G1_T3.user.model.User;
// import com.project.G1_T3.user.model.UserRole;
// import com.project.G1_T3.user.repository.UserRepository;
// import com.project.G1_T3.playerprofile.model.PlayerProfile;
// import com.project.G1_T3.common.glicko.Glicko2Result;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// class PlayerProfileServiceIntegrationTest {

//     @Autowired
//     private PlayerProfileService playerProfileService;

//     @Autowired
//     private PlayerRatingService playerRatingService;

//     @Autowired
//     private PlayerProfileRepository playerProfileRepository;

//     @Autowired UserRepository userRepository;

//     private User createAndSaveUser() {
//         User user = new User();
//         user.setUserId(UUID.randomUUID());
//         user.setUsername("test-" + UUID.randomUUID().toString().substring(0, 8));
//         user.setEmail("test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
//         user.setPasswordHash("password123");
//         user.setRole(UserRole.PLAYER);
//         return userRepository.save(user);
//     }

//     @Test
//     void testRatingUpdateAndPositionFinding() {
//         // Arrange
//         // Create and save player profiles

//         User user1 = createAndSaveUser();

//         PlayerProfile player1 = new PlayerProfile();
//         player1.setUser(user1);
//         player1.setGlickoRating(1500);
//         playerProfileRepository.save(player1);

//         User user2 = createAndSaveUser();

//         PlayerProfile player2 = new PlayerProfile();
//         player2.setUser(user2);
//         player2.setGlickoRating(1500);
//         playerProfileRepository.save(player2);

//         // Initialize buckets
//         playerRatingService.initializeBuckets();

//         // Act
//         // Simulate a match where player1 wins against player2
//         Glicko2Result result = new Glicko2Result(player2.getGlickoRating(), player2.getRatingDeviation(), 1.0);
//         List<Glicko2Result> results = Collections.singletonList(result);

//         playerProfileService.updatePlayerRating(player1.getProfileId(), results);

//         // Fetch updated profiles
//         PlayerProfile updatedPlayer1 = playerProfileService.findByProfileId(player1.getProfileId().toString());
//         int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(updatedPlayer1.getGlickoRating());

//         // Assert
//         assertTrue(updatedPlayer1.getGlickoRating() > 1500, "Player1's rating should have increased");
//         assertEquals(numberOfPlayersAhead, playerRatingService.getNumberOfPlayersAhead(updatedPlayer1.getGlickoRating()), "Number of players ahead should be accurate");
//     }

//     @Test
//     public void testConcurrentRatingUpdates() throws InterruptedException {
//         // Arrange
//         int numThreads = 10;
//         List<Thread> threads = new ArrayList<>();

//         User user = createAndSaveUser();

//         PlayerProfile player = new PlayerProfile();
//         player.setUser(user);
//         player.setGlickoRating(1500);
//         playerProfileRepository.save(player);

//         // Initialize buckets
//         playerRatingService.initializeBuckets();

//         // Act
//         // Simulate concurrent rating updates
//         for (int i = 0; i < numThreads; i++) {
//             Thread thread = new Thread(() -> {
//                 Glicko2Result result = new Glicko2Result(1500, 200, 1.0);
//                 List<Glicko2Result> results = Collections.singletonList(result);
//                 playerProfileService.updatePlayerRating(player.getProfileId(), results);
//             });
//             threads.add(thread);
//             thread.start();
//         }

//         // Wait for all threads to finish
//         for (Thread thread : threads) {
//             thread.join();
//         }

//         // Fetch updated profile
//         PlayerProfile updatedPlayer = playerProfileService.findByProfileId(player.getProfileId().toString());
//         int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(updatedPlayer.getGlickoRating());

//         // Assert
//         assertNotNull(updatedPlayer);
//         assertTrue(updatedPlayer.getGlickoRating() > 1500, "Player's rating should have increased after multiple updates");
//         assertEquals(numberOfPlayersAhead, playerRatingService.getNumberOfPlayersAhead(updatedPlayer.getGlickoRating()), "Number of players ahead should be accurate");
//     }

//     @Test
//     public void testPlayerRankPercentile() {
//         // Arrange
//         // Clear existing data
//         playerProfileRepository.deleteAll();

//         // Initialize RatingService
//         playerRatingService.initializeBuckets();

//         // Create players with known ratings
//         User user1 = createAndSaveUser();

//         PlayerProfile player1 = new PlayerProfile();
//         player1.setUser(user1);
//         player1.setGlickoRating(1500);
//         playerProfileRepository.save(player1);

//         User user2 = createAndSaveUser();

//         PlayerProfile player2 = new PlayerProfile();
//         player2.setUser(user2);
//         player2.setGlickoRating(1600);
//         playerProfileRepository.save(player2);

//         User user3 = createAndSaveUser();

//         PlayerProfile player3 = new PlayerProfile();
//         player3.setUser(user3);
//         player3.setGlickoRating(1400);
//         playerProfileRepository.save(player3);

//         User user4 = createAndSaveUser();

//         PlayerProfile player4 = new PlayerProfile();
//         player4.setUser(user4);
//         player4.setGlickoRating(1500);
//         playerProfileRepository.save(player4);

//         // Total players: 4
//         // Ratings distribution:
//         // - 1600: 1 player
//         // - 1500: 2 players
//         // - 1400: 1 player

//         // Act
//         // Calculate rank percentile for player1 (rating 1500)
//         double percentilePlayer1 = playerProfileService.getPlayerRank(player1.getProfileId().toString());

//         // Calculate expected percentile:
//         // Players ahead of rating 1500: players with rating > 1500
//         // Number of players ahead: 1 (player with rating 1600)
//         // Total players: 4
//         // Expected percentile:
//         // ((4 - 1) / 4) * 100 = (3 / 4) * 100 = 75%

//         // Assert
//         assertEquals(75.0, percentilePlayer1, 0.001, "Player1 should be at the 75th percentile");

//         // Calculate rank percentile for player2 (rating 1600)
//         double percentilePlayer2 = playerProfileService.getPlayerRank(player2.getProfileId().toString());

//         // Expected percentile:
//         // Players ahead of rating 1600: 0
//         // Percentile: ((4 - 0) / 4) * 100 = 100%

//         assertEquals(100.0, percentilePlayer2, 0.001, "Player2 should be at the 100th percentile");

//         // Calculate rank percentile for player3 (rating 1400)
//         double percentilePlayer3 = playerProfileService.getPlayerRank(player3.getProfileId().toString());

//         // Expected percentile:
//         // Players ahead: 3 (players with ratings 1600 and 1500)
//         // Percentile: ((4 - 3) / 4) * 100 = 25%

//         assertEquals(25.0, percentilePlayer3, 0.001, "Player3 should be at the 25th percentile");
//     }

// }
