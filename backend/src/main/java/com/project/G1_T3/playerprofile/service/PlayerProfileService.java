package com.project.G1_T3.playerprofile.service;

import com.project.G1_T3.security.service.AuthorizationService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public PlayerProfile save(PlayerProfile profile) {
        return playerProfileRepository.save(profile);
    }

    public int getPlayerRank(String profileId) {
        List<PlayerProfile> sortedPlayers = getSortedPlayerProfiles();

        // Find the player's rank in the sorted list
        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getProfileId().toString().equals(profileId)) {
                return i + 1; // Rank is 1-based index
            }
        }
        return -1; // Return -1 if the player is not found
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
}
