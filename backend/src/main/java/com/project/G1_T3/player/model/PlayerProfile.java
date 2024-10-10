package com.project.G1_T3.player.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_profiles")
public class PlayerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // Use java.time.LocalDate for DATE fields

    @Column(name = "country")
    private String country;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "current_rating")
    private Float currentRating;

    // Path that profile picture is stored in
    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    // Getters, setters, and other methods...
    public Integer getProfileId() {
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
}
