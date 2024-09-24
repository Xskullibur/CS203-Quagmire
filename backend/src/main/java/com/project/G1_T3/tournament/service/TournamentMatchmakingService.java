package com.project.G1_T3.tournament.service;

import com.project.G1_T3.match.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;

public class TournamentMatchmakingService {

    private final MatchmakingService matchmakingService;

    public TournamentMatchmakingService() {
        this.matchmakingService = new MatchmakingService();
    }

    public void createTournamentDraw(List<PlayerProfile> players) {
        List<PlayerProfile> draw = matchmakingService.generateTournamentDraw(players);
        // Logic to save the draw or create matches in the tournament
        // For now, you can print the draw for testing purposes
        draw.forEach(player -> System.out.println(player.getGlickoRating()));
    }
}