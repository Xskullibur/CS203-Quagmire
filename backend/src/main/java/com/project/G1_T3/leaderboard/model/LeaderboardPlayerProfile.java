package com.project.G1_T3.leaderboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.project.G1_T3.playerprofile.model.PlayerProfile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardPlayerProfile {

    UUID profileId;
    String firstName;
    String lastName;
    int glickoRating;
    Integer position;
    Double rankPercentage;

    public LeaderboardPlayerProfile(UUID profileId, String firstName, String lastName, int glickoRating) {
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.glickoRating = glickoRating;
    }

    public LeaderboardPlayerProfile(PlayerProfile player, Integer position) {
        profileId = player.getProfileId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        glickoRating = Math.round(player.getGlickoRating());
        this.position = position;
    }

    public LeaderboardPlayerProfile(PlayerProfile player, Double rankPercentage) {
        profileId = player.getProfileId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        glickoRating = Math.round(player.getGlickoRating());
        this.rankPercentage = rankPercentage;
    }

}
