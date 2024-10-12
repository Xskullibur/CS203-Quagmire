package com.project.G1_T3.player.model;

import java.time.LocalDate;
import java.util.UUID;

public class PlayerProfileDTO {
    private UUID profileId;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private String bio;
    private Float currentRating;
    // Other necessary fields

    public PlayerProfileDTO(UUID profileId, String username, String firstName, String lastName, LocalDate dateOfBirth,
            String country, String bio, Float currentRating) {
        this.profileId = profileId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.bio = bio;
        this.currentRating = currentRating;
    }

    public PlayerProfileDTO(PlayerProfile playerProfile) {
        this.profileId = playerProfile.getProfileId();
        this.username = playerProfile.getUsername();
        this.firstName = playerProfile.getFirstName();
        this.lastName = playerProfile.getLastName();
        this.dateOfBirth = playerProfile.getDateOfBirth();
        this.country = playerProfile.getCountry();
        this.bio = playerProfile.getBio();
        this.currentRating = playerProfile.getCurrentRating();
    }

    // Constructor, getters, and setters

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(Float currentRating) {
        this.currentRating = currentRating;
    }
}