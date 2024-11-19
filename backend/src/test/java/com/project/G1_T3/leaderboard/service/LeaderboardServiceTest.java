package com.project.G1_T3.leaderboard.service;

import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {


    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    @BeforeEach
    public void setUp() {
        // Initialization if needed
    }

    @Test
    void testGetTop10LeaderboardPlayerProfiles() {
        // Arrange
        PlayerProfile player1 = new PlayerProfile();
        player1.setProfileId(UUID.randomUUID());
        player1.setFirstName("Alice");
        player1.setLastName("Smith");
        player1.setGlickoRating(2000);

        PlayerProfile player2 = new PlayerProfile();
        player2.setProfileId(UUID.randomUUID());
        player2.setFirstName("Bob");
        player2.setLastName("Johnson");
        player2.setGlickoRating(1950);

        List<PlayerProfile> mockPlayerProfiles = Arrays.asList(player1, player2);

        // Mock the PlayerProfileService to return top 10 profiles
        when(playerProfileService.getTop10Players()).thenReturn(mockPlayerProfiles);

        // Act
        List<LeaderboardPlayerProfile> result = leaderboardService.getTop10LeaderboardPlayerProfiles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        LeaderboardPlayerProfile firstPlayer = result.get(0);
        assertEquals(player1.getProfileId(), firstPlayer.getProfileId());
        assertEquals("Alice", firstPlayer.getFirstName());
        assertEquals("Smith", firstPlayer.getLastName());
        assertEquals(2000, firstPlayer.getGlickoRating(), 0.001);
    }

    @Test
    void testGetPlayerInfo() {
        // Arrange
        String username = "alice_smith";
        UUID userId = UUID.randomUUID();

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername(username);

        PlayerProfile mockPlayerProfile = new PlayerProfile();
        mockPlayerProfile.setProfileId(UUID.randomUUID());
        mockPlayerProfile.setUser(mockUser);
        mockPlayerProfile.setFirstName("Alice");
        mockPlayerProfile.setLastName("Smith");
        mockPlayerProfile.setGlickoRating(2000);

        List<PlayerProfile> top10Profiles = Arrays.asList(mockPlayerProfile);
        when(userService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(playerProfileService.findByUserId(userId)).thenReturn(mockPlayerProfile);
        when(playerProfileService.getTop10Players()).thenReturn(top10Profiles);

        // Act
        LeaderboardPlayerProfile result = leaderboardService.getPlayerInfo(username);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlayerProfile.getProfileId(), result.getProfileId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(2000, result.getGlickoRating(), 0.001);
        assertEquals(1, result.getPosition());
    }

    @Test
    void testGetPlayerInfo_UserNotFound() {
        // Arrange
        String username = "nonexistent_user";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> leaderboardService.getPlayerInfo(username));
    }
    
    
    @Test
    void testGetPlayerInfo_UserNotInTop10() {
        // Arrange
        String username = "john_doe";
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername(username);

        UUID profileId = UUID.fromString("11111111-1111-1111-1111-111111111112");
        PlayerProfile mockPlayerProfile = new PlayerProfile();
        mockPlayerProfile.setProfileId(profileId);
        mockPlayerProfile.setUser(mockUser);
        mockPlayerProfile.setFirstName("John");
        mockPlayerProfile.setLastName("Doe");
        mockPlayerProfile.setGlickoRating(1800); // A rating that would not be in the top 10

        List<PlayerProfile> top10Profiles = Arrays.asList(); // Empty top 10 list to simulate the user not being in the top 10

        // Mock user retrieval
        when(userService.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Mock PlayerProfile retrieval by user ID
        when(playerProfileService.findByUserId(userId)).thenReturn(mockPlayerProfile);

        // Mock top 10 list to be empty or not include this player
        when(playerProfileService.getTop10Players()).thenReturn(top10Profiles);

        // Mock player rank percentage retrieval
        double rankPercentage = 85.0; // Assume this is the calculated rank for this user
        when(playerProfileService.getPlayerRank(profileId)).thenReturn(rankPercentage);

        // Act
        LeaderboardPlayerProfile result = leaderboardService.getPlayerInfo(username);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlayerProfile.getProfileId(), result.getProfileId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(1800, result.getGlickoRating(), 0.001);
        assertNull(result.getPosition(), "Position should be null because the player is not in the top 10");
        assertEquals(rankPercentage, result.getRankPercentage(), 0.001, "Rank percentage should match the calculated value");

        // Verify interactions with mocks
        verify(userService).findByUsername(username);
        verify(playerProfileService).findByUserId(userId);
        verify(playerProfileService).getTop10Players();
        verify(playerProfileService).getPlayerRank(mockPlayerProfile.getProfileId());
    }
}