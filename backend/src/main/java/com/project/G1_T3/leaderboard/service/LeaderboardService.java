package com.project.G1_T3.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private UserService userService;

    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles() {

        List<LeaderboardPlayerProfile> top10Players;

        top10Players = getTop10PlayerProfiles().stream()
                .map(profile -> new LeaderboardPlayerProfile(profile.getProfileId(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        profile.getCurrentRating()))
                .collect(Collectors.toList());

        return top10Players;
    }

    private List<PlayerProfile> getTop10PlayerProfiles() {
        return playerProfileRepository.findTop10ByOrderByCurrentRatingDesc();
    }


    // LeaderboardService.java
    public LeaderboardPlayerProfile getPlayerInfo(String username) {
        Optional<User> user = userService.findByUsername(username);
        UUID userId = user.get().getUserId();
        PlayerProfile player = playerProfileRepository.findByUserId(userId);
        long position = playerProfileRepository.getPositionOfPlayer(userId);
        return new LeaderboardPlayerProfile(player, position);
    }

    public LeaderboardPlayerProfile getPlayerInfoById(String userId) {
        UUID uuid = UUID.fromString(userId);
        PlayerProfile player = playerProfileRepository.findByUserId(uuid);
        long position = playerProfileRepository.getPositionOfPlayer(uuid);
        return new LeaderboardPlayerProfile(player, position);
    }

}
