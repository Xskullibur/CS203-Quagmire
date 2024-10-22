package com.project.G1_T3.player.model;

public class PlayerProfileRequest {
    private String id;
    private PlayerProfileDTO profileUpdates;

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
}