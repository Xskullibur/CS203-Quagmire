package com.project.G1_T3.match.service;

import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;
import java.util.stream.Collectors;

public class MatchmakingService {

    public List<PlayerProfile> generateTournamentDraw(List<PlayerProfile> players) {
        // Sort players based on their Glicko ratings (skill level)
        players.sort((p1, p2) -> Float.compare(p2.getGlickoRating(), p1.getGlickoRating())); // Descending order

        // Logic for generating pairs or matchups for the tournament
        // Example: Simply return the sorted list for now
        return players;
    }

    public List<PlayerProfile> findFriendlyMatches(PlayerProfile requester, List<PlayerProfile> potentialMatches) {
        // Filter potentialMatches based on location and skill level
        return potentialMatches.stream()
            .filter(p -> !p.equals(requester) && 
                         p.getCommunity().equals(requester.getCommunity()) && 
                         Math.abs(p.getGlickoRating() - requester.getGlickoRating()) < 100) // Example skill difference
            .collect(Collectors.toList());
    }
}
