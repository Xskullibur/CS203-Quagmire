package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import java.util.*;

import com.project.G1_T3.player.model.PlayerProfile;

@Service
public class PlayerProfileService {

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<PlayerProfile> findAll() {
        try {
            return playerProfileRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error fetching all player profiles", e);
        }
    }

    public PlayerProfile findByUserId(UUID id) {
        PlayerProfile profile = playerProfileRepository.findByUserId(id);
        if (profile == null) {
            throw new EntityNotFoundException("Player profile not found for userId: " + id);
        }
        return profile;
    }

    public PlayerProfile findByUserId(String id) {
        try {
            UUID userId = UUID.fromString(id);
            return findByUserId(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for userId: " + id, e);
        }
    }

    public PlayerProfile findByProfileId(UUID id) {
        try {
            PlayerProfile profile = playerProfileRepository.findByProfileId(id);
            if (profile == null) {
                throw new EntityNotFoundException("Player profile not found for profileId: " + id);
            }
            return profile;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for profileId: " + id, e);
        }
    }

    public PlayerProfile findByProfileId(String id) {
        try {
            UUID profileId = UUID.fromString(id);
            PlayerProfile profile = playerProfileRepository.findByProfileId(profileId);
            if (profile == null) {
                throw new EntityNotFoundException("Player profile not found for profileId: " + id);
            }
            return profile;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for profileId: " + id, e);
        }
    }

    public PlayerProfile save(PlayerProfile profile) {
        try {
            return playerProfileRepository.save(profile);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error saving player profile", e);
        }
    }

    @Cacheable(value = "playerRankings", key = "'rankings'")
    public List<PlayerProfile> getSortedPlayerProfiles() {
        try {
            return playerProfileRepository.findAllByOrderByCurrentRatingDesc();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error fetching sorted player profiles", e);
        }
    }

    public int getPlayerRank(String profileId) {
        try {
            List<PlayerProfile> sortedPlayers = getSortedPlayerProfiles();

            for (int i = 0; i < sortedPlayers.size(); i++) {
                if (sortedPlayers.get(i).getProfileId().toString().equals(profileId)) {
                    return i + 1; // Rank is 1-based index
                }
            }
            return -1; // Return -1 if the player is not found
        } catch (Exception e) {
            throw new RuntimeException("Error getting player rank for profileId: " + profileId, e);
        }
    }

    @CacheEvict(value = "playerRankings", key = "'rankings'")
    public PlayerProfile updatePlayerRating(PlayerProfile playerProfile) {
        try {
            return playerProfileRepository.save(playerProfile);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error updating player rating", e);
        }
    }

    // For editing profile
    public PlayerProfile updateProfile(UUID id, PlayerProfile profileUpdates) {
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
