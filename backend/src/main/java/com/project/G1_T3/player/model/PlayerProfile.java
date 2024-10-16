package com.project.G1_T3.player.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.G1_T3.tournament.model.Tournament;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth; // Use java.time.LocalDate for DATE fields

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "community", nullable = true)
    private String community;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "glicko_rating", nullable = false)
    private float glickoRating = (float) 1500.0;

    @Column(name = "rating_deviation", nullable = false)
    private float ratingDeviation = (float) 350.0;

    @Column(name = "volatility", nullable = false)
    private float volatility = (float) 0.06; // Glicko-2

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

    public String getCommunity() {
        return community;
    }

    public float getELO() {
        return glickoRating;
    }

    public void setELO(float glickoRating) {
        this.glickoRating = glickoRating;
    }

    public float getDeviation() {
        return ratingDeviation;
    }

    public void setDeviation(float ratingDeviation) {
        this.ratingDeviation = ratingDeviation;
    }

    public float getVolatility() {
        return volatility;
    }

    public void setVolatility(float volatility) {
        this.volatility = volatility;
    }

    // Override equals() method for proper comparison in matchmaking
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PlayerProfile))
            return false;
        PlayerProfile other = (PlayerProfile) obj;
        return this.glickoRating == other.glickoRating &&
                this.community.equals(other.community);
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

    @ManyToMany(mappedBy = "players", cascade = CascadeType.ALL)
    private Set<Tournament> tournaments = new HashSet<>();

    public String getUsername() {
        return firstName + " " + lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Float getCurrentRating() {
        return currentRating;
    }

    public PlayerProfileDTO getProfile() {
        return new PlayerProfileDTO(this);
    }

}
