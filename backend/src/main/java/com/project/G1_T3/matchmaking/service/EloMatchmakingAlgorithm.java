package com.project.G1_T3.matchmaking.service;

import org.springframework.stereotype.Component;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;

/**
 * EloMatchmakingAlgorithm is a matchmaking algorithm that matches players based on their Elo rating,
 * geographical distance, and queue time.
 */
@Component
public class EloMatchmakingAlgorithm implements MatchmakingAlgorithm {
    private static final int MAX_RATING_DIFFERENCE = 200; // Maximum allowed rating difference between players
    private static final int MAX_DISTANCE_KM = 50; // Maximum allowed distance in kilometers between players
    private static final int MAX_QUEUE_TIME_SECONDS = 300; // Maximum queue time in seconds (5 minutes)

    /**
     * Determines if two players are a good match based on their rating, distance, and queue time.
     *
     * @param player1 The first queued player.
     * @param player2 The second queued player.
     * @return true if the players are a good match, false otherwise.
     */
    @Override
    public boolean isGoodMatch(QueuedPlayer player1, QueuedPlayer player2) {
        int ratingDifference = (int) Math.abs(player1.getPlayer().getGlickoRating() - player2.getPlayer().getGlickoRating());
        double distance = calculateDistance(player1, player2);
        long maxQueueTime = Math.max(player1.getQueueTimeSeconds(), player2.getQueueTimeSeconds());

        // Players are a good match if their rating difference and distance are within limits,
        // or if either player has been in the queue for a long time.
        return (ratingDifference <= MAX_RATING_DIFFERENCE && distance <= MAX_DISTANCE_KM) ||
                maxQueueTime >= MAX_QUEUE_TIME_SECONDS;
    }

    /**
     * Calculates the geographical distance between two players using the Haversine formula.
     *
     * @param player1 The first queued player.
     * @param player2 The second queued player.
     * @return The distance between the two players in kilometers.
     */
    private double calculateDistance(QueuedPlayer player1, QueuedPlayer player2) {
        final int R = 6371; // Earth's radius in kilometers

        double lat1 = Math.toRadians(player1.getLatitude());
        double lon1 = Math.toRadians(player1.getLongitude());
        double lat2 = Math.toRadians(player2.getLatitude());
        double lon2 = Math.toRadians(player2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula to calculate the distance between two points on the Earth's surface
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
