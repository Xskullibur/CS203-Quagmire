package com.project.G1_T3.player;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;

import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.round.service.RoundServiceImpl;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class PlayerProfileServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testFindAllSuccess() {
        // Arrange
        List<PlayerProfile> profiles = Arrays.asList(new PlayerProfile(), new PlayerProfile());
        when(playerProfileRepository.findAll()).thenReturn(profiles);

        // Act
        List<PlayerProfile> result = playerProfileService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(playerProfileRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllFailure() {
        // Arrange
        when(playerProfileRepository.findAll()).thenThrow(new DataAccessException("...") {
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> playerProfileService.findAll());
        assertEquals("Error fetching all player profiles", exception.getMessage());
    }

    @Test
    public void testFindByUserIdSuccess() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(); // Create a mock user
        PlayerProfile profile = new PlayerProfile();

        // Mock the userService to return the user
        when(userService.findByUserId(userId.toString())).thenReturn(Optional.of(user));

        // Mock the playerProfileRepository to return the profile for that user
        when(playerProfileRepository.findByUser(user)).thenReturn(profile);

        // Act
        PlayerProfile result = playerProfileService.findByUserId(userId);

        // Assert
        assertNotNull(result);
        verify(userService, times(1)).findByUserId(userId.toString());
        verify(playerProfileRepository, times(1)).findByUser(user);
    }

    @Test
    public void testFindByUserIdUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userService.findByUserId(userId.toString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> playerProfileService.findByUserId(userId));
        verify(userService, times(1)).findByUserId(userId.toString());
    }

    @Test
    public void testFindByUserIdFailure() {
        // Arrange
        UUID userId = UUID.randomUUID();
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> playerProfileService.findByUserId(userId));
        assertEquals("User not found for user ID: " + userId, exception.getMessage());
    }

    @Test
    public void testFindByProfileIdInvalidUUID() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> playerProfileService.findByProfileId(UUID.fromString("invalid-uuid")));
        assertEquals("Invalid UUID string: invalid-uuid", exception.getMessage());
    }

    @Test
    public void testSaveSuccess() {
        // Arrange
        PlayerProfile profile = new PlayerProfile();
        when(playerProfileRepository.save(profile)).thenReturn(profile);

        // Act
        PlayerProfile result = playerProfileService.save(profile);

        // Assert
        assertNotNull(result);
        verify(playerProfileRepository, times(1)).save(profile);
    }

    @Test
    public void testSaveFailure() {
        // Arrange
        PlayerProfile profile = new PlayerProfile();
        when(playerProfileRepository.save(profile)).thenThrow(new DataAccessException("...") {
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> playerProfileService.save(profile));
        assertEquals("Error saving player profile", exception.getMessage());
    }

    // @Test
    // public void testGetPlayerRankSuccess() {
    // // Arrange
    // List<PlayerProfile> sortedProfiles = Arrays.asList(
    // new PlayerProfile(UUID.randomUUID(), UUID.randomUUID(), "Player1",
    // "LastName1", LocalDate.now(), "USA", "Community1", "Bio1", 2000, 350f, 0.06f,
    // 0f, null, new HashSet<>()),
    // new PlayerProfile(UUID.randomUUID(), UUID.randomUUID(), "Player2",
    // "LastName2", LocalDate.now(), "Canada", "Community2", "Bio2", 1500, 350f,
    // 0.06f, 0f, null, new HashSet<>()),
    // new PlayerProfile(UUID.randomUUID(), UUID.randomUUID(), "Player3",
    // "LastName3", LocalDate.now(), "UK", "Community3", "Bio3", 1000, 350f, 0.06f,
    // 0f, null, new HashSet<>())
    // );
    // when(playerProfileService.getSortedPlayerProfiles()).thenReturn(sortedProfiles);

    // // Act
    // int rank =
    // playerProfileService.getPlayerRank(sortedProfiles.get(1).getProfileId().toString());

    // // Assert
    // assertEquals(2, rank);
    // }

    @Test
    public void testUpdatePlayerRatingSuccess() {
        // Arrange
        PlayerProfile profile = new PlayerProfile();
        when(playerProfileRepository.save(profile)).thenReturn(profile);

        // Act
        PlayerProfile result = playerProfileService.updatePlayerRating(profile);

        // Assert
        assertNotNull(result);
        verify(playerProfileRepository, times(1)).save(profile);
    }

    @Test
    public void testUpdatePlayerRatingFailure() {
        // Arrange
        PlayerProfile profile = new PlayerProfile();
        when(playerProfileRepository.save(profile)).thenThrow(new DataAccessException("Error updating player rating") {
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> playerProfileService.updatePlayerRating(profile));
        assertEquals("Error updating player rating", exception.getMessage());
    }
}
