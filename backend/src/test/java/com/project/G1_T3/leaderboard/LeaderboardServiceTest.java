package com.project.G1_T3.leaderboard;


import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.leaderboard.service.LeaderboardServiceImpl;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(playerProfileRepository.findTop10ByOrderByGlickoRatingDesc()).thenReturn(mockPlayerProfiles);

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

        long position = 1L;

        when(userService.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(playerProfileRepository.findByUserId(userId)).thenReturn(mockPlayerProfile);
        when(playerProfileRepository.getPositionOfPlayer(userId)).thenReturn(position);

        // Act
        LeaderboardPlayerProfile result = leaderboardService.getPlayerInfo(username);

        // Assert
        assertNotNull(result);
        assertEquals(mockPlayerProfile.getProfileId(), result.getProfileId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(2000, result.getGlickoRating(), 0.001);
        assertEquals(position, result.getPosition());
    }

    @Test
    void testGetPlayerInfo_UserNotFound() {
        // Arrange
        String username = "nonexistent_user";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            leaderboardService.getPlayerInfo(username);
        });
    }

    // Additional tests can be added here
}
