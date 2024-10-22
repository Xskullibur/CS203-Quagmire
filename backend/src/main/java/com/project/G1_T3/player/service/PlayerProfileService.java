package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.project.G1_T3.common.glicko.Glicko2Result;
import com.project.G1_T3.player.model.PlayerProfile;

@Service
public class PlayerProfileService {

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private PlayerRatingService playerRatingService;

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

    public PlayerProfile createPlayer(PlayerProfile profile) {
        int rating = profile.getGlickoRating();
        playerRatingService.addPlayer(rating);

        return save(profile);
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
