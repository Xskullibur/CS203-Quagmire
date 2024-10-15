package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.project.G1_T3.player.model.PlayerProfile;

@Service
public class PlayerProfileService {
    
    
    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<PlayerProfile> findAll() {
        return playerProfileRepository.findAll();
    }

    public PlayerProfile findByUserId(String id){
        return playerProfileRepository.findByUserId(UUID.fromString(id));
    }

    public PlayerProfile findByProfileId(String id){
        return playerProfileRepository.findByProfileId(UUID.fromString(id));
    }

    public PlayerProfile save(PlayerProfile profile){
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

}
