package com.project.G1_T3.leaderboard.service;

import org.springframework.stereotype.Service;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;

import java.util.List;

@Service
public interface LeaderboardService {

    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles();

    // LeaderboardService.java
    public LeaderboardPlayerProfile getPlayerInfo(String username);

    public LeaderboardPlayerProfile getPlayerInfoById(String userId);

}
