package com.project.G1_T3.playerprofile.service;

import com.project.G1_T3.security.service.AuthorizationService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.service.AchievementService;
import com.project.G1_T3.common.exception.ProfileAlreadyExistException;
import com.project.G1_T3.common.glicko.Glicko2Result;
import com.project.G1_T3.filestorage.service.FileStorageService;
import com.project.G1_T3.filestorage.service.ImageValidationService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;

@Service
public class PlayerProfileService {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ImageValidationService imageValidationService;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private PlayerRatingService playerRatingService;

    public List<PlayerProfile> findAll() {
        return playerProfileRepository.findAll();
    }

    public PlayerProfile findByUserId(String id) {
        return userService.findByUserId(id).map(user -> playerProfileRepository.findByUser(user))
                .orElseThrow(() -> new EntityNotFoundException("User not found for user ID: " + id));
    }

    public PlayerProfile findByProfileId(String id) {
        return playerProfileRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new EntityNotFoundException("Player profile not found for ID: " + id));
    }


    public double getPlayerRank(String profileId) {
        // Get the player's profile
        PlayerProfile playerProfile = playerProfileRepository.findByProfileId(UUID.fromString(profileId));
        if (playerProfile == null) {
            // Handle player not found
            throw new NoSuchElementException("Player with ID " + profileId + " not found.");
        }

        int playerRating = playerProfile.getGlickoRating();
        int numberOfPlayersAhead = playerRatingService.getNumberOfPlayersAhead(playerRating);
        int totalPlayers = playerRatingService.getTotalPlayers();

        double rankPercentage = ((double) (totalPlayers - numberOfPlayersAhead)) / totalPlayers * 100;

        return rankPercentage;
    }

    @Cacheable(value = "playerRankings", key = "'rankings'")
    public List<PlayerProfile> getSortedPlayerProfiles() {
        // Fetch all players sorted by current rating
        return playerProfileRepository.findAllByOrderByCurrentRatingDesc();
    }

    @CacheEvict(value = "playerRankings", key = "'rankings'")
    public PlayerProfile updatePlayerRating(PlayerProfile playerProfile) {
        // Update the player's rating (e.g., after a match)
        // Invalidate cache for player rankings
        return playerProfileRepository.save(playerProfile);
    }

    // For editing profile
    public PlayerProfile updateProfile(UUID id, PlayerProfileDTO profileUpdates,
            MultipartFile profileImage) throws IOException {

        // Check if the user is who they claim they are
        User user = authorizationService.authorizeUserById(id);

        // Retrieve the existing profile
        PlayerProfile existingProfile = playerProfileRepository.findByUser(user);

        // Throw an exception if the profile is not found
        if (existingProfile == null) {
            throw new EntityNotFoundException("Player profile not found for user ID: " + id);
        }

        // Upload Image
        if (profileImage != null) {
            String profileImagePath = uploadProfileImage(id.toString(), profileImage);
            existingProfile.setProfilePicturePath(profileImagePath);
        }

        // Update fields
        if (profileUpdates.getFirstName() != null) {
            existingProfile.setFirstName(profileUpdates.getFirstName());
        }
        if (profileUpdates.getLastName() != null) {
            existingProfile.setLastName(profileUpdates.getLastName());
        }
        if (profileUpdates.getBio() != null) {
            existingProfile.setBio(profileUpdates.getBio());
        }
        if (profileUpdates.getCountry() != null) {
            existingProfile.setCountry(profileUpdates.getCountry());
        }
        if (profileUpdates.getDateOfBirth() != null) {
            existingProfile.setDateOfBirth(profileUpdates.getDateOfBirth());
        }

        // Save the updated profile
        return playerProfileRepository.save(existingProfile);
    }

    public PlayerProfile createProfile(UUID id, PlayerProfileDTO profileUpdates,
            MultipartFile profileImage) throws IOException {

        // Check if the user is who they claim they are
        User user = authorizationService.authorizeUserById(id);

        // Retrieve the existing profile
        PlayerProfile existingProfile = playerProfileRepository.findByUser(user);

        // Throw an exception if the profile is not found
        if (existingProfile != null) {
            throw new ProfileAlreadyExistException("Player profile already exist for user ID: " + id);
        }

        // Save the updated profile
        PlayerProfile newProfile = PlayerProfile.fromDTO(profileUpdates);
        newProfile.setUser(user);
        return playerProfileRepository.save(newProfile);
    }

    private String uploadProfileImage(String userId, MultipartFile profileImage)
            throws IOException {

        // Validate image if present
        if (profileImage != null && !profileImage.isEmpty()) {
            imageValidationService.validateImage(profileImage);
        }

        return fileStorageService.uploadFile("ProfileImages", userId, profileImage);
    }

    // For uploading profile photo
    public PlayerProfile updateProfilePicture(UUID id, String profilePicturePath) {
        PlayerProfile profile = playerProfileRepository.findByUserId(id);
        profile.setProfilePicturePath(profilePicturePath);
        return playerProfileRepository.save(profile);
    }

    public PlayerProfile findByUsername(String username) {

        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        Optional<PlayerProfile> optionalPlayerProfile = optionalUser
                .map(user -> playerProfileRepository.findByUser(user));

        if (optionalPlayerProfile.isEmpty()) {
            throw new EntityNotFoundException("Player profile not found for username: " + username);
        }

        return optionalPlayerProfile.get();
    }

    public Set<Achievement> getPlayerAchievements(String username) {
        // Fetch the user by username & player by userId
        Optional<User> user = userService.findByUsername(username);
        UUID userId = user.get().getUserId();
        PlayerProfile player = playerProfileRepository.findByUserId(userId);

        // Run the achievement check first
        achievementService.checkAchievements(player);

        // Return the achievements set from PlayerProfile
        return player.getAchievements();
    }

    public Set<Tournament> getPlayerTournaments(String username) {
        // Fetch the user by username & player by userId
        Optional<User> user = userService.findByUsername(username);
        UUID userId = user.get().getUserId();
        PlayerProfile player = playerProfileRepository.findByUserId(userId);

        // Refresh achievements for that player
        achievementService.checkAchievements(player);

        // Return the achievements set from PlayerProfile
        return player.getTournaments();
    }

    public void updatePlayerRating(UUID playerId, List<Glicko2Result> results) {
        Optional<PlayerProfile> playerOpt = playerProfileRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            PlayerProfile playerProfile = playerOpt.get();
            int oldRating = playerProfile.getGlickoRating();

            playerProfile.updateRating(results);

            int newRating = playerProfile.getGlickoRating();
            playerProfileRepository.save(playerProfile);

            // Update the rating counts
            playerRatingService.updateRating(oldRating, newRating);
        } else {
            throw new NoSuchElementException("Player with ID " + playerId + " not found.");
        }
    }

}
