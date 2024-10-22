package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.security.service.SecurityService;
import com.project.G1_T3.user.model.CustomUserDetails;

import io.jsonwebtoken.security.SecurityException;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;

@Service
public class PlayerProfileService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<PlayerProfile> findAll() {
        return playerProfileRepository.findAll();
    }

    public PlayerProfile findByUserId(String id) {
        return playerProfileRepository.findByUserId(UUID.fromString(id));
    }

    public PlayerProfile findByProfileId(String id) {
        return playerProfileRepository.findByProfileId(UUID.fromString(id));
    }

    public PlayerProfile save(PlayerProfile profile) {
        return playerProfileRepository.save(profile);
    }

    @Cacheable(value = "playerRankings", key = "'rankings'")
    public List<PlayerProfile> getSortedPlayerProfiles() {
        // Fetch all players sorted by current rating
        return playerProfileRepository.findAllByOrderByCurrentRatingDesc();
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

    @CacheEvict(value = "playerRankings", key = "'rankings'")
    public PlayerProfile updatePlayerRating(PlayerProfile playerProfile) {
        // Update the player's rating (e.g., after a match)
        // Invalidate cache for player rankings
        return playerProfileRepository.save(playerProfile);
    }

    // For editing profile
    public PlayerProfile updateProfile(UUID id, PlayerProfileDTO profileUpdates) {

        CustomUserDetails userDetails = securityService.getAuthenticatedUser();

        if (userDetails == null || !userDetails.getUser().getId().equals(id)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        PlayerProfile existingProfile = playerProfileRepository.findByUserId(id);

        // Throw an exception if the profile is not found
        if (existingProfile == null) {
            throw new EntityNotFoundException("Player profile not found for user ID: " + id);
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

    // For uploading profile photo
    public PlayerProfile updateProfilePicture(UUID id, String profilePicturePath) {
        PlayerProfile profile = playerProfileRepository.findByUserId(id);
        profile.setProfilePicturePath(profilePicturePath);
        return playerProfileRepository.save(profile);
    }
}
