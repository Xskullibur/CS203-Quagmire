package com.project.G1_T3.leaderboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardPlayerProfile {
    
    Integer profileId;
    String firstName;
    String lastName;
    Float ELO;

}
