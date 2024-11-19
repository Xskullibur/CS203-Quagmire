package com.project.G1_T3.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {


    @Autowired
    private PlayerProfileService playerProfileService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves the top 10 players sorted by Glicko rating
     * and maps them to LeaderboardPlayerProfile objects.
     *
     * @return List of top 10 LeaderboardPlayerProfile objects.
     */
    public List<LeaderboardPlayerProfile> getTop10LeaderboardPlayerProfiles() {

        List<LeaderboardPlayerProfile> top10Players;

        top10Players = getTop10PlayerProfiles().stream()
                .map(profile -> new LeaderboardPlayerProfile(profile.getProfileId(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        Math.round(profile.getGlickoRating())))
                .collect(Collectors.toList());

        return top10Players;
    }

    /**
     * Retrieves the top 10 player profiles ordered by Glicko rating.
     *
     * @return List of top 10 PlayerProfile entities.
     */
    public List<PlayerProfile> getTop10PlayerProfiles() {
        return playerProfileService.getTop10Players();
    }

    /**
     * Retrieves a player's leaderboard information using their username.
     *
     * @param username The username of the player.
     * @return The LeaderboardPlayerProfile object for the specified player.
     */
    public LeaderboardPlayerProfile getPlayerInfo(String username) {
        Optional<User> user = userService.findByUsername(username);
        UUID userId = user.get().getUserId();
        return getPlayerInfoById(userId.toString());
    }

    /**
     * Retrieves a player's leaderboard information using their user ID.
     *
     * @param userId The UUID of the player.
     * @return The LeaderboardPlayerProfile object for the specified player.
     */
    public LeaderboardPlayerProfile getPlayerInfoById(String userId) {
        UUID uuid = UUID.fromString(userId);
        PlayerProfile player = playerProfileService.findByUserId(uuid);
        UUID playerId = player.getProfileId();

        List<UUID> top10PlayerIds = getTop10PlayerProfiles().stream().map(PlayerProfile::getProfileId).collect(Collectors.toList());

        Integer position = null;
        for(int i = 0; i < top10PlayerIds.size(); i++){
            if(playerId.equals(top10PlayerIds.get(i))){
                position = i + 1;
            }
        }

        if(position != null){
            return new LeaderboardPlayerProfile(player, position);
        }

        Double rankPercentage = playerProfileService.getPlayerRank(playerId);
        return new LeaderboardPlayerProfile(player, rankPercentage);
    }
}
