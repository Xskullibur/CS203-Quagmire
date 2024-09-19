package com.project.G1_T3.player.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_profiles")
public class PlayerProfile {

    @Id
    @GeneratedValue()
    private UUID profileId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth; // Use java.time.LocalDate for DATE fields

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "current_rating")
    private Float currentRating = 0.0f;

    // Getters, setters, and other methods...
    public UUID getUserId() {
        return userId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Float getELO() {
        return currentRating;
    }

    public double getRating() {
        return currentRating;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRating(Float rating) {
        this.currentRating = rating;
    }
}
