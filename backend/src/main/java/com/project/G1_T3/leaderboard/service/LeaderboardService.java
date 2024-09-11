package com.project.G1_T3.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles() {

        List<LeaderboardPlayerProfile> top10Players;

        top10Players = getTop10PlayerProfiles().stream()
                .map(profile -> new LeaderboardPlayerProfile(profile.getProfileId(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        profile.getELO()))
                .collect(Collectors.toList());

        return top10Players;
    }

    private List<PlayerProfile> getTop10PlayerProfiles() {
        return playerProfileRepository.findTop10ByOrderByCurrentRatingDesc();
    }


    public LeaderboardPlayerProfile getPlayerInfo(long userId){
        PlayerProfile player = playerProfileRepository.getPlayerProfileByUserId(userId);
        LeaderboardPlayerProfile result = new LeaderboardPlayerProfile(player, playerProfileRepository.getPositionOfPlayer(userId));
        return result;
    }

}
