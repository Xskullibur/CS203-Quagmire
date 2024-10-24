// GlickoMatchmaking.java
package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlickoMatchmaking implements MatchmakingAlgorithm {
    private static final double MAX_RATING_DIFFERENCE = 300.0; // Maximum acceptable rating difference
    private static final double MAX_DEVIATION_DIFF = 100.0; // Maximum acceptable RD difference
    private static final double MAX_DISTANCE_KM = 2.0; // Maximum distance in kilometers

    private final LocationService locationService;

    @Autowired
    public GlickoMatchmaking(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public boolean isGoodMatch(QueuedPlayer player1, QueuedPlayer player2) {
        // Check rating difference
        double ratingDiff = Math.abs(player1.getPlayer().getGlickoRating() -
                player2.getPlayer().getGlickoRating());

        // Check rating deviation difference
        double deviationDiff = Math.abs(player1.getPlayer().getRatingDeviation() -
                player2.getPlayer().getRatingDeviation());

        // Calculate geographical distance
        double distance = locationService.calculateDistance(
                player1.getLatitude(), player1.getLongitude(),
                player2.getLatitude(), player2.getLongitude());

        boolean isMatch = ratingDiff <= MAX_RATING_DIFFERENCE &&
                deviationDiff <= MAX_DEVIATION_DIFF &&
                distance <= MAX_DISTANCE_KM;

        log.debug("Match check: Rating diff={}, RD diff={}, Distance={}, isMatch={}",
                ratingDiff, deviationDiff, distance, isMatch);

        return isMatch;
    }

    public double getMaxRatingDiff() {
        return MAX_RATING_DIFFERENCE;
    }

    public double getMaxDeviationDiff() {
        return MAX_DEVIATION_DIFF;
    }

    public double getMaxDistanceKm() {
        return MAX_DISTANCE_KM;
    }
}