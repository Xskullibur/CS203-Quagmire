package com.project.G1_T3.playerprofile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.security.service.AuthorizationService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.filestorage.service.FileStorageService;
import com.project.G1_T3.filestorage.service.ImageValidationService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.achievement.model.*;
import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.service.AchievementService;
import com.project.G1_T3.common.exception.ProfileAlreadyExistException;
import com.project.G1_T3.common.glicko.Glicko2Result;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PlayerProfileServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private MultipartFile profileImage;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @Mock
    private ImageValidationService imageValidationService;

    @Mock
    private PlayerRatingService playerRatingService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    private UUID userId;
    private PlayerProfile existingProfile;
    private PlayerProfileDTO profileUpdates;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userId = UUID.randomUUID();

        // Set up User
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        // Set up existing profile
        existingProfile = new PlayerProfile();
        existingProfile.setUser(user);
        existingProfile.setFirstName("John");
        existingProfile.setLastName("Doe");
        existingProfile.setBio("Original bio");
        existingProfile.setCountry("USA");
        existingProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        existingProfile.setCurrentRating(1500);

        // Set up profile updates
        profileUpdates = new PlayerProfileDTO();
        profileUpdates.setFirstName("Jane");
        profileUpdates.setLastName("Smith");
        profileUpdates.setBio("Updated bio");
        profileUpdates.setCountry("Canada");
        profileUpdates.setDateOfBirth(LocalDate.of(1992, 2, 2));
    }

    @Test
    void findAll_WithExistingProfiles_ReturnsProfileList() {
        List<PlayerProfile> expectedProfiles = Arrays.asList(existingProfile);
        when(playerProfileRepository.findAll()).thenReturn(expectedProfiles);

        List<PlayerProfile> actualProfiles = playerProfileService.findAll();

        assertEquals(expectedProfiles, actualProfiles);
        verify(playerProfileRepository).findAll();
    }

    @Test
    void findByUserId_WithValidUserId_ReturnsProfile() {
        when(userService.findByUserId(userId.toString())).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUser(user)).thenReturn(existingProfile);

        PlayerProfile result = playerProfileService.findByUserId(userId);

        assertNotNull(result);
        assertEquals(existingProfile, result);
    }

    @Test
    void findByUserId_WithInvalidUserId_ThrowsException() {
        when(userService.findByUserId(userId.toString())).thenReturn(Optional.empty());

        Supplier<PlayerProfile> findByUserIdCall = () -> playerProfileService.findByUserId(userId);

        assertThrows(EntityNotFoundException.class, findByUserIdCall::get);
    }

    @Test
    void findByUsername_WithValidUserId_ReturnsProfile() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUser(user)).thenReturn(existingProfile);

        PlayerProfile result = playerProfileService.findByUsername(user.getUsername());

        assertNotNull(result);
        assertEquals(existingProfile, result);
    }

    @Test
    void findByUsername_WithInvalidUserId_ThrowsException() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Supplier<PlayerProfile> findByUserIdCall = () -> playerProfileService.findByUsername(user.getUsername());

        assertThrows(UsernameNotFoundException.class, findByUserIdCall::get);
    }

    @Test
    void findByUsername_NoPlayerProfile_ThrowsException() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUser(user)).thenReturn(null);

        Supplier<PlayerProfile> findByUserIdCall = () -> playerProfileService.findByUsername(user.getUsername());

        assertThrows(EntityNotFoundException.class, findByUserIdCall::get);
    }

    @Test
    void findByProfileId_WithValidId_ReturnsProfile() {
        UUID profileId = UUID.randomUUID();
        when(playerProfileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));

        PlayerProfile result = playerProfileService.findByProfileId(profileId);

        assertNotNull(result);
        assertEquals(existingProfile, result);
    }

    @Test
    void findByProfileId_WithInvalidId_ThrowsException() {
        UUID profileId = UUID.randomUUID();
        when(playerProfileRepository.findById(profileId)).thenReturn(Optional.empty());

        Supplier<PlayerProfile> findByProfileIdCall = () -> playerProfileService.findByProfileId(profileId);

        assertThrows(EntityNotFoundException.class, findByProfileIdCall::get);
    }

    @Test
    void getPlayerRank_WithValidProfile_ReturnsCorrectRank() {
        UUID profileId = UUID.randomUUID();
        existingProfile.setProfileId(profileId);

        // Mock findByProfileId to return the profile
        when(playerProfileRepository.findByProfileId(profileId))
                .thenReturn(existingProfile);

        // Mock the rating service methods
        when(playerRatingService.getNumberOfPlayersAhead(anyInt())).thenReturn(0);
        when(playerRatingService.getNumberOfPlayersInBucket(anyInt())).thenReturn(1);
        when(playerRatingService.getTotalPlayers()).thenReturn(1);

        double rank = playerProfileService.getPlayerRank(profileId);

        assertEquals(100.0, rank); // Should return 100% since it's the only player

        // Verify the mocks were called
        verify(playerProfileRepository).findByProfileId(profileId);
        verify(playerRatingService).getNumberOfPlayersAhead(anyInt());
        verify(playerRatingService).getTotalPlayers();
    }

    @Test
    void updateProfile_WithValidInputs_ReturnsUpdatedProfile() throws IOException {
        when(authorizationService.authorizeUserById(userId)).thenReturn(user);
        when(playerProfileRepository.findByUser(user)).thenReturn(existingProfile);
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);
        when(fileStorageService.uploadFile(eq("ProfileImages"), any(), any()))
                .thenReturn("path/to/image");

        PlayerProfile updatedProfile = playerProfileService.updateProfile(userId, profileUpdates, profileImage);

        assertNotNull(updatedProfile);
        assertEquals(profileUpdates.getFirstName(), updatedProfile.getFirstName());
        assertEquals(profileUpdates.getLastName(), updatedProfile.getLastName());
        assertEquals(profileUpdates.getBio(), updatedProfile.getBio());
        assertEquals(profileUpdates.getCountry(), updatedProfile.getCountry());
        assertEquals(profileUpdates.getDateOfBirth(), updatedProfile.getDateOfBirth());
    }

    @Test
    void updateProfile_WithUnauthorizedUser_ThrowsException() {
        when(authorizationService.authorizeUserById(userId))
                .thenThrow(new SecurityException("User not authorized"));

        Supplier<PlayerProfile> updateProfileCall = () -> {
            try {
                return playerProfileService.updateProfile(userId, profileUpdates, profileImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        assertThrows(SecurityException.class, updateProfileCall::get);
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updateProfile_WithNonexistentProfile_ThrowsException() {
        when(authorizationService.authorizeUserById(userId)).thenReturn(user);
        when(playerProfileRepository.findByUser(user)).thenReturn(null);

        Supplier<PlayerProfile> updateProfileCall = () -> {
            try {
                return playerProfileService.updateProfile(userId, profileUpdates, profileImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        assertThrows(EntityNotFoundException.class, updateProfileCall::get);
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void createProfile_WithValidInputs_ReturnsNewProfile() throws IOException {
        when(authorizationService.authorizeUserById(userId)).thenReturn(user);
        when(playerProfileRepository.findByUser(user)).thenReturn(null);
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);

        PlayerProfile createdProfile = playerProfileService.createProfile(userId, profileUpdates, profileImage);

        assertNotNull(createdProfile);
        verify(playerProfileRepository).save(any(PlayerProfile.class));
    }

    @Test
    void createProfile_WithExistingProfile_ThrowsException() {
        when(authorizationService.authorizeUserById(userId)).thenReturn(user);
        when(playerProfileRepository.findByUser(user)).thenReturn(existingProfile);

        Supplier<PlayerProfile> createProfileCall = () -> {
            try {
                return playerProfileService.createProfile(userId, profileUpdates, profileImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        assertThrows(ProfileAlreadyExistException.class, createProfileCall::get);
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updatePlayerRating_WithValidProfile_ReturnsUpdatedProfile() {
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);

        PlayerProfile updatedProfile = playerProfileService.updatePlayerRating(existingProfile);

        assertNotNull(updatedProfile);
        verify(playerProfileRepository).save(existingProfile);
    }

    @Test
    void getTop10Players_ReturnsTop10ProfilesOrderedByRating() {
        // Arrange
        List<UUID> top10PlayerIds = new ArrayList<>();
        List<PlayerProfile> mockProfiles = new ArrayList<>();

        // Generate 10 players with descending ratings
        for (int i = 0; i < 10; i++) {
            UUID playerId = UUID.randomUUID();
            top10PlayerIds.add(playerId);

            PlayerProfile playerProfile = new PlayerProfile();
            playerProfile.setProfileId(playerId);
            playerProfile.setGlickoRating(2000 - i); // Descending ratings from 2000 to 1991
            mockProfiles.add(playerProfile);
        }

        // Mock the playerRatingService to return the top 10 UUIDs
        when(playerRatingService.getTop10Players()).thenReturn(top10PlayerIds);

        // Mock playerProfileRepository to return a profile for each UUID
        for (PlayerProfile profile : mockProfiles) {
            when(playerProfileRepository.findById(profile.getProfileId()))
                    .thenReturn(Optional.of(profile));
        }

        // Act
        List<PlayerProfile> result = playerProfileService.getTop10Players();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size(), "The result should contain exactly 10 players");

        // Verify that the players are ordered by Glicko rating in descending order
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getGlickoRating() >= result.get(i + 1).getGlickoRating(),
                    "Players should be ordered by Glicko rating in descending order");
        }

        // Verify that the returned profiles match the mockProfiles by ID and rating
        for (int i = 0; i < result.size(); i++) {
            assertEquals(mockProfiles.get(i).getProfileId(), result.get(i).getProfileId(),
                    "The profile ID at index " + i + " should match the expected profile");
            assertEquals(mockProfiles.get(i).getGlickoRating(), result.get(i).getGlickoRating(),
                    "The profile rating at index " + i + " should match the expected rating");
        }

        // Verify the calls to the repository
        verify(playerRatingService, times(1)).getTop10Players();
        for (UUID playerId : top10PlayerIds) {
            verify(playerProfileRepository, times(1)).findById(playerId);
        }
    }

    @Test
    void rankRetrival_ReturnsCorrectValuesWhenManyProfiles() {
        UUID profileId = UUID.randomUUID();
        existingProfile.setProfileId(profileId);
        existingProfile.setGlickoRating(1500); // Set the rating of the main profile

        List<PlayerProfile> playersAbove1500 = new ArrayList<>();
        List<PlayerProfile> playersBelow1500 = new ArrayList<>();

        // Create 700 profiles with a rating above 1500
        for (int i = 0; i < 700; i++) {
            PlayerProfile current = new PlayerProfile();
            current.setProfileId(UUID.randomUUID());
            current.setGlickoRating(1600 + i);
            playersAbove1500.add(current);
        }

        // Create 500 profiles with a rating below 1500
        for (int i = 0; i < 500; i++) {
            PlayerProfile current = new PlayerProfile();
            current.setProfileId(UUID.randomUUID());
            current.setGlickoRating(1400 - i);
            playersBelow1500.add(current);
        }

        // Mock repository to return the main profile when findByProfileId is called
        when(playerProfileRepository.findByProfileId(profileId)).thenReturn(existingProfile);

        // Mock PlayerRatingService interactions
        when(playerRatingService.getNumberOfPlayersAhead(1500)).thenReturn(playersAbove1500.size());
        when(playerRatingService.getNumberOfPlayersInBucket(1500)).thenReturn(3); // Assume 
        when(playerRatingService.getTotalPlayers()).thenReturn(playersAbove1500.size() + playersBelow1500.size() + 3);

        // Act
        double rankPercentage = playerProfileService.getPlayerRank(profileId);

        // Calculate expected rank percentage
        int totalPlayers = playersAbove1500.size() + playersBelow1500.size() + 3; // 1203 players in total
        double expectedRankPercentage = ((double) playersAbove1500.size() + 3) / totalPlayers * 100;

        // Assert
        assertEquals(expectedRankPercentage, rankPercentage, 0.01,
                "The rank percentage should be calculated correctly");

        // Verify interactions with mocks
        verify(playerProfileRepository).findByProfileId(profileId);
        verify(playerRatingService).getNumberOfPlayersAhead(1500);
        verify(playerRatingService).getNumberOfPlayersInBucket(1500);
        verify(playerRatingService).getTotalPlayers();
    }

    @Test
    void getPlayerRankByUsername_WithValidUsername_ReturnsCorrectRank() {
        String username = "testuser";

        UUID existingProfileId = UUID.randomUUID();
        existingProfile.setProfileId(existingProfileId);
        // Mock user and profile retrieval
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUserId(user.getId())).thenReturn(existingProfile);
        when(playerProfileRepository.findByProfileId(existingProfileId)).thenReturn(existingProfile);

        // Mock rank calculations
        when(playerRatingService.getNumberOfPlayersAhead(anyInt())).thenReturn(0);
        when(playerRatingService.getNumberOfPlayersInBucket(anyInt())).thenReturn(1);
        when(playerRatingService.getTotalPlayers()).thenReturn(1);

        // Act
        double rank = playerProfileService.getPlayerRankByUsername(username);

        // Assert
        assertEquals(100.0, rank, 0.01); // Should return 100% since it's the only player
        verify(userService).findByUsername(username);
        verify(playerProfileRepository).findByUserId(user.getId());
        verify(playerRatingService).getNumberOfPlayersAhead(anyInt());
        verify(playerRatingService).getTotalPlayers();
    }

    @Test
    void updatePlayerRating_WithManualOverride_AppliesCorrectValue() {
        UUID playerId = UUID.randomUUID();
        existingProfile.setProfileId(playerId);
        existingProfile.setGlickoRating(1500);

        List<Glicko2Result> results = new ArrayList<>(); // Mock results
        Glicko2Result mockResult = mock(Glicko2Result.class);
        results.add(mockResult);

        // Stub repository
        when(playerProfileRepository.findById(playerId)).thenReturn(Optional.of(existingProfile));

        // Act
        playerProfileService.updatePlayerRating(playerId, results);

        // Manually override the rating
        int newRating = 1550;
        existingProfile.setGlickoRating(newRating);

        // Assert
        assertEquals(newRating, Math.round(existingProfile.getGlickoRating()));
    }

    @Test
    void updatePlayerRating_WithValidPlayerIdAndResults_UpdatesRatingAndCounts() {
        UUID playerId = UUID.randomUUID();
        existingProfile.setProfileId(playerId);
        existingProfile.setGlickoRating(1500);

        List<Glicko2Result> results = new ArrayList<>(); // Mock results
        Glicko2Result mockResult = mock(Glicko2Result.class); // opponent has 0 rating, 0 score, 0 deviation
        results.add(mockResult);

        int oldRating = Math.round(existingProfile.getGlickoRating());

        // Stub repository to return the actual profile
        when(playerProfileRepository.findById(playerId)).thenReturn(Optional.of(existingProfile));

        // Act
        playerProfileService.updatePlayerRating(playerId, results);

        int newRating = 795;
        // Assert
        assertEquals(newRating, Math.round(existingProfile.getGlickoRating()));
        verify(playerProfileRepository).findById(playerId);
        verify(playerProfileRepository).save(existingProfile);
        verify(playerRatingService).updateRating(playerId, oldRating, newRating);
    }

    @Test
    void getPlayerRank_WithValidProfileId_ReturnsCorrectRank() {
        UUID profileId = UUID.randomUUID();
        existingProfile.setProfileId(profileId);

        when(playerProfileRepository.findByProfileId(profileId)).thenReturn(existingProfile);
        when(playerRatingService.getNumberOfPlayersAhead(anyInt())).thenReturn(50);
        when(playerRatingService.getNumberOfPlayersInBucket(anyInt())).thenReturn(1);
        when(playerRatingService.getTotalPlayers()).thenReturn(101);

        double rank = playerProfileService.getPlayerRank(profileId);

        double expectedRank = (((double) 50 + 1) / 101) * 100;
        assertEquals(expectedRank, rank, 0.01, "The calculated rank should match the expected value");

        verify(playerProfileRepository).findByProfileId(profileId);
        verify(playerRatingService).getNumberOfPlayersAhead(anyInt());
        verify(playerRatingService).getNumberOfPlayersInBucket(anyInt());
        verify(playerRatingService).getTotalPlayers();
    }

    @Test
    void getPlayerAchievements_WithValidUsername_ReturnsAchievements() {
        String username = "testuser";
        Set<Achievement> achievements = Set.of(new Achievement());
        existingProfile.setAchievements(achievements);

        // Mock user retrieval
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUserId(user.getId())).thenReturn(existingProfile);

        // Act
        Set<Achievement> result = playerProfileService.getPlayerAchievements(username);

        // Assert
        assertNotNull(result);
        assertEquals(achievements, result);
        verify(achievementService).checkAchievements(existingProfile); // Ensure achievement check is called
        verify(userService).findByUsername(username);
        verify(playerProfileRepository).findByUserId(user.getId());
    }

    @Test
    void getPlayerTournaments_WithValidUsername_ReturnsAllTournaments() {
        String username = "testuser";
        Set<Tournament> tournaments = Set.of(new Tournament());
        existingProfile.setTournaments(tournaments);

        // Mock user and player profile retrieval
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(playerProfileRepository.findByUserId(user.getId())).thenReturn(existingProfile);

        // Act
        Set<Tournament> result = playerProfileService.getPlayerTournaments(username);

        // Assert
        assertNotNull(result);
        assertEquals(tournaments, result, "The returned tournaments should match the player's tournaments");
        verify(achievementService).checkAchievements(existingProfile);
    }

    @Test
    void getPlayerAchievements_WithNullUserOrProfile_ThrowsEntityNotFoundException() {
        String username = "nonexistentuser";

        // Mock user retrieval to return empty
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> playerProfileService.getPlayerAchievements(username));
        verify(userService).findByUsername(username);
    }

}
