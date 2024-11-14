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
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.filestorage.service.FileStorageService;
import com.project.G1_T3.filestorage.service.ImageValidationService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.common.exception.ProfileAlreadyExistException;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

}
