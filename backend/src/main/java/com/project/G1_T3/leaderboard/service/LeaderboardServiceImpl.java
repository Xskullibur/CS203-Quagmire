package com.project.G1_T3.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

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

        // Convert the top 10 PlayerProfile entities to LeaderboardPlayerProfile DTOs
        top10Players = getTop10PlayerProfiles().stream()
                .map(profile -> new LeaderboardPlayerProfile(profile.getProfileId(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        profile.getGlickoRating()))
                .collect(Collectors.toList());

        return top10Players;
    }

    /**
     * Retrieves the top 10 player profiles ordered by Glicko rating.
     *
     * @return List of top 10 PlayerProfile entities.
     */
    public List<PlayerProfile> getTop10PlayerProfiles() {
        return playerProfileRepository.findTop10ByOrderByGlickoRatingDesc();
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

        // Retrieve player profile using userId and get their position in the leaderboard
        PlayerProfile player = playerProfileRepository.findByUserId(userId);
        long position = playerProfileRepository.getPositionOfPlayer(userId);
        
        // Return the player information wrapped in a LeaderboardPlayerProfile object
        return new LeaderboardPlayerProfile(player, position);
    }

    /**
     * Retrieves a player's leaderboard information using their user ID.
     *
     * @param userId The UUID of the player.
     * @return The LeaderboardPlayerProfile object for the specified player.
     */
    public LeaderboardPlayerProfile getPlayerInfoById(String userId) {
        UUID uuid = UUID.fromString(userId);

        // Retrieve player profile using UUID and get their position in the leaderboard
        PlayerProfile player = playerProfileRepository.findByUserId(uuid);
        long position = playerProfileRepository.getPositionOfPlayer(uuid);
        
        // Return the player information wrapped in a LeaderboardPlayerProfile object
        return new LeaderboardPlayerProfile(player, position);
    }
}
