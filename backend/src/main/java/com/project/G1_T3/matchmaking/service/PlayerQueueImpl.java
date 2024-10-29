package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerQueueImpl implements PlayerQueue {

    @Autowired
    private final MatchmakingKDTree kdTree;

    private final PriorityBlockingQueue<QueuedPlayer> priorityQueue;
    private static final int K_NEIGHBORS = 5; // Number of nearest neighbors to consider

    @Autowired
    GlickoMatchmaking glickoMatchmaking;

    public PlayerQueueImpl(MatchmakingKDTree kdTree, GlickoMatchmaking glickoMatchmaking) {
        this.kdTree = kdTree;
        this.priorityQueue = new PriorityBlockingQueue<>(11,
                Comparator.comparingLong(QueuedPlayer::getQueueTimeSeconds));
        this.glickoMatchmaking = glickoMatchmaking;
    }

    @Override
    public void addPlayer(PlayerProfile player, double latitude, double longitude) {
        QueuedPlayer queuedPlayer = new QueuedPlayer(player, latitude, longitude);
        kdTree.insert(queuedPlayer);
        priorityQueue.offer(queuedPlayer);
    }

    public QueuedPlayer findMatch(QueuedPlayer player) {
        // Remove the player from the queue to avoid matching with themselves
        kdTree.remove(player);
        priorityQueue.remove(player);

        List<QueuedPlayer> potentialMatches = findPotentialMatches(player);

        for (QueuedPlayer match : potentialMatches) {
            if (glickoMatchmaking.isGoodMatch(player, match)) {
                // Remove the matched player from the queue
                kdTree.remove(match);
                priorityQueue.remove(match);
                return match;
            }
        }

        // If no suitable match is found, re-add the player to the queue
        kdTree.insert(player);
        priorityQueue.offer(player);
        return null;
    }

    private List<QueuedPlayer> findPotentialMatches(QueuedPlayer player) {
        PriorityQueue<Map.Entry<QueuedPlayer, Double>> nearestNeighbors = kdTree.findKNearest(
                player,
                glickoMatchmaking.getMaxRatingDiff(),
                glickoMatchmaking.getMaxDeviationDiff(),
                glickoMatchmaking.getMaxDistanceKm(),
                K_NEIGHBORS);

        List<QueuedPlayer> result = new ArrayList<>();
        while (!nearestNeighbors.isEmpty()) {
            QueuedPlayer potentialMatch = nearestNeighbors.poll().getKey();
            if (!potentialMatch.equals(player)) {
                result.add(potentialMatch);
            }
        }

        return result;
    }

    @Override
    public QueuedPlayer pollPlayer() {
        QueuedPlayer player = priorityQueue.poll();
        if (player != null) {
            kdTree.remove(player);
            return player;
        }
        return null;
    }

    @Override
    public int size() {
        return priorityQueue.size();
    }

    public void removePlayer(UUID playerId) {
        kdTree.removeByPlayerId(playerId);
        priorityQueue.removeIf(player -> player.getPlayer().getUser().getId().equals(playerId));
    }

    public boolean containsPlayer(UUID playerId) {
        return kdTree.containsPlayer(playerId);
    }

    public List<QueuedPlayer> getAllPlayers() {
        return new ArrayList<>(priorityQueue);
    }
}