package com.project.G1_T3.player.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.project.G1_T3.player.model.PlayerProfile;

@Service
public interface PlayerProfileService {

    public List<PlayerProfile> findAll();

    public PlayerProfile findByUserId(UUID id);

    public PlayerProfile findByUserId(String id);

    public PlayerProfile findByProfileId(UUID id);

    public PlayerProfile findUserByUsername(String username);

    public PlayerProfile findByProfileId(String id);

    public PlayerProfile save(PlayerProfile profile);

    @Cacheable(value = "playerRankings", key = "'rankings'")
    public List<PlayerProfile> getSortedPlayerProfiles();

    public int getPlayerRank(String profileId);

    @CacheEvict(value = "playerRankings", key = "'rankings'")
    public PlayerProfile updatePlayerRating(PlayerProfile playerProfile);

    // For editing profile
    public PlayerProfile updateProfile(UUID id, PlayerProfile profileUpdates);

    // For uploading profile photo
    public PlayerProfile updateProfilePicture(UUID id, String profilePicturePath);
}
