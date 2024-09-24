package com.project.G1_T3.match.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchResult;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.GlickoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    private final GlickoService glickoService;

    @Autowired
    public MatchService(GlickoService glickoService) {
        this.glickoService = glickoService;
    }

    // Method to handle match outcomes
    public void handleMatchOutcome(MatchResult matchResult) {
        PlayerProfile player1 = matchResult.getWinner(); // Assuming MatchResult contains winner and loser
        PlayerProfile player2 = matchResult.getLoser();

        // Update ratings based on the match outcome
        glickoService.updateRatings(player1, player2, true); // true if player1 won, false otherwise
    }

    // Additional methods for match management can be added here
    public void createMatch(Match match) {
        // Logic to create and save the match in the database
    }

    public MatchResult getMatchResult(Long matchId) {
        // Logic to retrieve the match result from the database
        return null; // Placeholder
    }

    // Other match-related methods can be implemented here
}
