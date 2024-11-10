// package com.project.G1_T3.leaderboard.service;

// import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
// import com.project.G1_T3.player.model.PlayerProfile;
// import com.project.G1_T3.player.repository.PlayerProfileRepository;
// import com.project.G1_T3.user.model.User;
// import com.project.G1_T3.user.service.UserService;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import java.util.Collections;
// import java.util.List;
// import java.util.NoSuchElementException;
// import java.util.Optional;
// import java.util.UUID;
// import java.util.ArrayList;
// import java.util.Arrays;

// @ExtendWith(MockitoExtension.class)
// public class LeaderboardServiceTest {

//     @Mock
//     private PlayerProfileRepository playerProfileRepository;

//     @Mock
//     private UserService userService;

//     @InjectMocks
//     private LeaderboardServiceImpl leaderboardService;

//     private PlayerProfile createPlayerProfile(String firstName, String lastName, int glickoRating, float ratingDeviation, float volatility, float currentRating) {
//         PlayerProfile playerProfile = new PlayerProfile();
//         playerProfile.setProfileId(UUID.randomUUID());
//         playerProfile.setUserId(UUID.randomUUID());
//         playerProfile.setFirstName(firstName);
//         playerProfile.setLastName(lastName);
//         playerProfile.setCountry("US");
//         playerProfile.setCommunity("Gaming");
//         playerProfile.setBio("Top player");
//         playerProfile.setGlickoRating(glickoRating);
//         playerProfile.setRatingDeviation(ratingDeviation);
//         playerProfile.setVolatility(volatility);
//         playerProfile.setCurrentRating(currentRating);
//         playerProfile.setProfilePicturePath("path/to/image");

//         // Optionally set other fields if needed...

//         return playerProfile;
//     }

//     private List<PlayerProfile> createPlayerProfilesList(int count, String firstName, String lastName, int startRating) {
//         List<PlayerProfile> playerProfiles = new ArrayList<>();
//         for (int i = 0; i < count; i++) {
//             playerProfiles.add(createPlayerProfile(
//                 firstName, 
//                 lastName + i, // Unique last name to differentiate
//                 startRating + i * 10, // Incremental rating
//                 300.0f,       // ratingDeviation
//                 0.05f,        // volatility
//                 startRating + i * 10 // currentRating matching glickoRating
//             ));
//         }
//         return playerProfiles;
//     }

//     // getTop10LeaderboardPlayerProfiles

//     @Test
//     public void getTop10LeaderboardPlayerProfiles_fewerThan10Players_returnAllPlayers() {
//         List<PlayerProfile> fewerPlayers = createPlayerProfilesList(2, "Ant", "Tan", 1500);
//         when(playerProfileRepository.findTop10ByOrderByCurrentRatingDesc()).thenReturn(fewerPlayers);

//         List<LeaderboardPlayerProfile> result = leaderboardService.getTop10LeaderboardPlayerProfiles();

//         assertEquals(1, result.size());
//         verify(playerProfileRepository, times(1)).findTop10ByOrderByCurrentRatingDesc();
//     }

//     @Test
//     public void getTop10LeaderboardPlayerProfiles_noPlayers_returnEmptyList() {
//         when(playerProfileRepository.findTop10ByOrderByCurrentRatingDesc()).thenReturn(Collections.emptyList());

//         List<LeaderboardPlayerProfile> result = leaderboardService.getTop10LeaderboardPlayerProfiles();

//         assertTrue(result.isEmpty());
//         verify(playerProfileRepository, times(1)).findTop10ByOrderByCurrentRatingDesc();
//     }

//     @Test
//     public void getTop10LeaderboardPlayerProfiles_exactly10Players_returnTop10Players() {
//         List<PlayerProfile> tenPlayers = createPlayerProfilesList(10, "Ant", "Tan", 1500);;
//         when(playerProfileRepository.findTop10ByOrderByCurrentRatingDesc()).thenReturn(tenPlayers);

//         List<LeaderboardPlayerProfile> result = leaderboardService.getTop10LeaderboardPlayerProfiles();

//         assertEquals(10, result.size());
//         verify(playerProfileRepository, times(1)).findTop10ByOrderByCurrentRatingDesc();
//     }

//     // getPlayerInfo

//     @Test
//     public void getPlayerInfo_usernameNotFound_throwException() {
//         String username = "nonexistentuser";
//         when(userService.findByUsername(username)).thenReturn(Optional.empty());

//         assertThrows(NoSuchElementException.class, () -> leaderboardService.getPlayerInfo(username));
//         verify(userService, times(1)).findByUsername(username);
//         verifyNoMoreInteractions(playerProfileRepository);
//     }

//     @Test
//     public void getPlayerInfo_profileNotFound_returnNull() {
//         String username = "testuser";
//         UUID userId = UUID.randomUUID();
//         when(userService.findByUsername(username)).thenReturn(Optional.of(new User(userId, username, "email@example.com", "password123")));
//         when(playerProfileRepository.findByUserId(userId)).thenReturn(null);

//         LeaderboardPlayerProfile result = leaderboardService.getPlayerInfo(username);

//         assertNull(result);
//         verify(userService, times(1)).findByUsername(username);
//         verify(playerProfileRepository, times(1)).findByUserId(userId);
//     }

//     @Test
//     public void getPlayerInfo_noLeaderboardPosition_returnProfileWithPositionZero() {
//         String username = "testuser";
//         UUID userId = UUID.randomUUID();
//         PlayerProfile profile = new PlayerProfile(userId, UUID.randomUUID(), "John", "Doe", null, "US", "Gaming", "Top player", 1600, 350, 0.06f, 1600, null, "path/to/image");

//         when(userService.findByUsername(username)).thenReturn(Optional.of(new User(userId, username, "email@example.com", "password123")));
//         when(playerProfileRepository.findByUserId(userId)).thenReturn(profile);
//         when(playerProfileRepository.getPositionOfPlayer(userId)).thenReturn(0L);

//         LeaderboardPlayerProfile result = leaderboardService.getPlayerInfo(username);

//         assertNotNull(result);
//         assertEquals(0L, result.getPosition());
//         verify(userService, times(1)).findByUsername(username);
//         verify(playerProfileRepository, times(1)).findByUserId(userId);
//         verify(playerProfileRepository, times(1)).getPositionOfPlayer(userId);
//     }

//     // getPlayerInfoById

//     @Test
//     public void getPlayerInfoById_userIdNotFound_throwException() {
//         String userId = UUID.randomUUID().toString();
//         UUID uuid = UUID.fromString(userId);

//         when(playerProfileRepository.findByUserId(uuid)).thenReturn(null);

//         assertThrows(NoSuchElementException.class, () -> leaderboardService.getPlayerInfoById(userId));
//         verify(playerProfileRepository, times(1)).findByUserId(uuid);
//     }

//     @Test
//     public void getPlayerInfoById_profileNotFound_returnNull() {
//         String userId = UUID.randomUUID().toString();
//         UUID uuid = UUID.fromString(userId);

//         when(playerProfileRepository.findByUserId(uuid)).thenReturn(null);

//         LeaderboardPlayerProfile result = leaderboardService.getPlayerInfoById(userId);

//         assertNull(result);
//         verify(playerProfileRepository, times(1)).findByUserId(uuid);
//     }

//     @Test
//     public void getPlayerInfoById_noLeaderboardPosition_returnProfileWithPositionZero() {
//         String userId = UUID.randomUUID().toString();
//         UUID uuid = UUID.fromString(userId);
//         PlayerProfile profile = new PlayerProfile(uuid, UUID.randomUUID(), "Jane", "Doe", null, "US", "Gaming", "Top player", 1550, 300, 0.05f, 1550, null, "path/to/image");

//         when(playerProfileRepository.findByUserId(uuid)).thenReturn(profile);
//         when(playerProfileRepository.getPositionOfPlayer(uuid)).thenReturn(0L);

//         LeaderboardPlayerProfile result = leaderboardService.getPlayerInfoById(userId);

//         assertNotNull(result);
//         assertEquals(0L, result.getPosition());
//         verify(playerProfileRepository, times(1)).findByUserId(uuid);
//         verify(playerProfileRepository, times(1)).getPositionOfPlayer(uuid);
//     }
// }
