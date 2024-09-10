package com.project.G1_T3.leaderboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.project.G1_T3.player.model.PlayerProfile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardPlayerProfile {
    
    Long profileId;
    String firstName;
    String lastName;
    Float ELO;
    Long position;

    public LeaderboardPlayerProfile(Long profileId, String firstName, String lastName, Float eLO) {
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
        ELO = eLO;
    }

    public LeaderboardPlayerProfile(PlayerProfile player, Long p){
        profileId = player.getProfileId();
        firstName = player.getFirstName();
        lastName = player.getLastName();
        ELO = player.getELO();
        position = p;
    }

    
}
