package com.project.G1_T3.player.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @ManyToMany(mappedBy = "players", cascade = CascadeType.ALL)
    private Set<Tournament> tournaments = new HashSet<>();

}
