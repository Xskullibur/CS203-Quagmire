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
    Long position;

    public LeaderboardPlayerProfile(UUID profileId, String firstName, String lastName, int glickoRating) {
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.glickoRating = glickoRating;
    }

    public LeaderboardPlayerProfile(PlayerProfile player, Long p) {
        profileId = player.getProfileId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        glickoRating = player.getGlickoRating();
        position = p;
    }

}
