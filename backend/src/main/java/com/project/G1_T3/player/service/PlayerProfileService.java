package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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

    public PlayerProfile findByUserId(Long id){
        return playerProfileRepository.findByUserId(id);
    }

    public PlayerProfile save(PlayerProfile profile){
        return playerProfileRepository.save(profile);
    }

    // For editing profile
    public PlayerProfile updateProfile(Long id, PlayerProfile profileUpdates) {
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
    public PlayerProfile updateProfilePicture(Long id, String profilePicturePath) {
        PlayerProfile profile = playerProfileRepository.findByUserId(id);
        profile.setProfilePicturePath(profilePicturePath);
        return playerProfileRepository.save(profile);
    }
}
