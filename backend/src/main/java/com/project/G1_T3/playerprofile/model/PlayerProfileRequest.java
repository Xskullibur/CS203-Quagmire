package com.project.G1_T3.playerprofile.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerProfileRequest {
    private String id;

    @JsonProperty("profileUpdates")
    private PlayerProfileDTO profileUpdates;
    
    private MultipartFile profileImage;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlayerProfileDTO getProfileUpdates() {
        return profileUpdates;
    }

    public void setProfileUpdates(PlayerProfileDTO profileUpdates) {
        this.profileUpdates = profileUpdates;
    }

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile file) {
        this.profileImage = file;
    }

}