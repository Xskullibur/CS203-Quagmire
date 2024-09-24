package com.project.G1_T3.player.service;

import com.project.G1_T3.player.model.PlayerProfile;

public class GlickoService {

    private static final double Q = Math.log(10) / 400; // Glicko constant
    private static final float K = 30.0f; // Example factor to adjust RD after a match

    public void updateRatings(PlayerProfile player1, PlayerProfile player2, boolean player1Won) {
        // Calculate the expected score for each player
        double expectedScore1 = calculateExpectedScore(player1, player2);
        double expectedScore2 = calculateExpectedScore(player2, player1);

        // Calculate the new RD for both players
        float newRD1 = calculateNewRD(player1);
        float newRD2 = calculateNewRD(player2);

        // Update ratings based on match outcome
        double score1 = player1Won ? 1.0 : 0.0;
        double score2 = player1Won ? 0.0 : 1.0;

        // Calculate new ratings
        player1.setELO(player1.getGlickoRating() + (float)(Q / (1 / Math.pow(newRD1, 2) + 1 / Math.pow(newRD2, 2)) * (score1 - expectedScore1)));
        player2.setELO(player2.getGlickoRating() + (float)(Q / (1 / Math.pow(newRD1, 2) + 1 / Math.pow(newRD2, 2)) * (score2 - expectedScore2)));

        // Update RD after the match
        player1.setRatingDeviation(newRD1);
        player2.setRatingDeviation(newRD2);
    }

    private double calculateExpectedScore(PlayerProfile player1, PlayerProfile player2) {
        return 1.0 / (1.0 + Math.pow(10, (player2.getGlickoRating() - player1.getGlickoRating()) / 400));
    }

    private float calculateNewRD(PlayerProfile player) {
        // Increase RD after every match played
        float newRD = Math.min(350.0f, player.getRatingDeviation() + K); // Increment RD
        return newRD;
    }

    private void updateVolatility(PlayerProfile player1, PlayerProfile player2) {
        // Implement logic to update volatility based on Glicko specifications
    }
}