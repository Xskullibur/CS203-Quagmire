package com.project.G1_T3.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface LeaderboardService {

    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles();

    public LeaderboardPlayerProfile getPlayerInfo(String username);

    public LeaderboardPlayerProfile getPlayerInfoById(String userId);

}
