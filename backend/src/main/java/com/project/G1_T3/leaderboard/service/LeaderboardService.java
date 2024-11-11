package com.project.G1_T3.leaderboard.service;

import java.util.List;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;

public interface LeaderboardService {

    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles();

    public List<PlayerProfile> getTop10PlayerProfiles();

    public LeaderboardPlayerProfile getPlayerInfo(String username);

    public LeaderboardPlayerProfile getPlayerInfoById(String userId);

}
