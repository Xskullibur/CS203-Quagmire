package com.project.G1_T3.playerprofile.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.common.glicko.Glicko2Rating;
import com.project.G1_T3.common.glicko.Glicko2Result;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private static final float DEVIATION_SCALE = 1500.0f;
    private static final float VOLATILITY_SCALE = 0.75f;

    @Id
    @GeneratedValue()
    private UUID profileId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

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
    private int glickoRating = 1500;

    @Column(name = "rating_deviation", nullable = false)
    private float ratingDeviation = (float) 350.0;

    @Column(name = "volatility", nullable = false)
    private float volatility = (float) 0.06; // Glicko-2

    @Column(name = "current_rating")
    private float currentRating = 0f;

    @Column()
    @Transient
    private Glicko2Rating glicko2Rating;

    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    @ManyToMany(mappedBy = "players", cascade = CascadeType.ALL)
    private Set<Tournament> tournaments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "player_achievements",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private Set<Achievement> achievements;

    public static PlayerProfile fromDTO(PlayerProfileDTO profileDTO) {
        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileId(profileDTO.getProfileId());
        playerProfile.setFirstName(profileDTO.getFirstName());
        playerProfile.setLastName(profileDTO.getLastName());
        playerProfile.setDateOfBirth(profileDTO.getDateOfBirth());
        playerProfile.setCountry(profileDTO.getCountry());
        playerProfile.setBio(profileDTO.getBio());
        playerProfile.setCurrentRating(profileDTO.getCurrentRating() != null ? profileDTO.getCurrentRating() : 0f);
        playerProfile.setProfilePicturePath(profileDTO.getProfileImagePath());
        return playerProfile;
    }

    // Override equals() method for proper comparison in matchmaking
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PlayerProfile)) {
            return false;
        }
        PlayerProfile other = (PlayerProfile) obj;
        return this.glickoRating == other.glickoRating && this.community.equals(other.community);
    }

    @PostLoad
    private void postLoad() {
        syncGlicko2Rating();
    }

    private void syncGlicko2Rating() {
        if (this.glicko2Rating == null) {
            this.glicko2Rating = new Glicko2Rating(this.glickoRating, this.ratingDeviation,
                    this.volatility);
            this.glicko2Rating = new Glicko2Rating(
                    this.glickoRating,
                    this.ratingDeviation,
                    this.volatility);
        } else {
            this.glicko2Rating.setRating((float) this.glickoRating);
            this.glicko2Rating.setRatingDeviation(this.ratingDeviation);
            this.glicko2Rating.setVolatility(this.volatility);
        }
    }

    public void setGlickoRating(int glickoRating) {
        this.glickoRating = glickoRating;
        syncGlicko2Rating();
    }

    public void setRatingDeviation(float ratingDeviation) {
        this.ratingDeviation = ratingDeviation;
        syncGlicko2Rating();
    }

    public void setVolatility(float volatility) {
        this.volatility = volatility;
        syncGlicko2Rating();
    }

    public void updateRating(List<Glicko2Result> results) {
        syncGlicko2Rating();
        glicko2Rating.updateRating(results);

        // Update fields with new values
        this.glickoRating = (int) Math.round(glicko2Rating.getRating());
        this.ratingDeviation = (float) glicko2Rating.getRatingDeviation();
        this.volatility = (float) glicko2Rating.getVolatility();

        // Update currentRating
        setCurrentRating();
    }

    public void setCurrentRating() {
        currentRating = glickoRating + DEVIATION_SCALE / ratingDeviation + VOLATILITY_SCALE / volatility;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public Float getCurrentRating() {
        return currentRating;
    }

    public PlayerProfileDTO getProfile() {
        return new PlayerProfileDTO(this);
    }

    public double getCurrentRD() {
        return ratingDeviation;
    }

}
