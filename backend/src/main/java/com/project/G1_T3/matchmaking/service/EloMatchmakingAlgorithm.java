package com.project.G1_T3.matchmaking.service;

import org.springframework.stereotype.Component;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;

@Component
public class EloMatchmakingAlgorithm implements MatchmakingAlgorithm {
    private static final int MAX_RATING_DIFFERENCE = 200;
    private static final int MAX_DISTANCE_KM = 50;
    private static final int MAX_QUEUE_TIME_SECONDS = 300; // 5 minutes

    @Override
    public boolean isGoodMatch(QueuedPlayer player1, QueuedPlayer player2) {
        int ratingDifference = (int) Math
                .abs(player1.getPlayer().getCurrentRating() - player2.getPlayer().getCurrentRating());
        double distance = calculateDistance(player1, player2);
        long maxQueueTime = Math.max(player1.getQueueTimeSeconds(), player2.getQueueTimeSeconds());

        return (ratingDifference <= MAX_RATING_DIFFERENCE && distance <= MAX_DISTANCE_KM) ||
                maxQueueTime >= MAX_QUEUE_TIME_SECONDS;
    }

    private double calculateDistance(QueuedPlayer player1, QueuedPlayer player2) {
        final int R = 6371; // Earth's radius in kilometers

        double lat1 = Math.toRadians(player1.getLatitude());
        double lon1 = Math.toRadians(player1.getLongitude());
        double lat2 = Math.toRadians(player2.getLatitude());
        double lon2 = Math.toRadians(player2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}