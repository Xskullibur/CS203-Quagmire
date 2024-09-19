package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.player.model.PlayerProfile;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MatchmakingService {
    private final ConcurrentMap<UUID, PlayerProfile> playerPool = new ConcurrentHashMap<>();

    public void addPlayerToQueue(PlayerProfile player) {
        System.out.println("Adding player to queue: " + player.getFirstName() + " (ID: " + player.getProfileId() + ")");
        playerPool.put(player.getProfileId(), player);
        System.out.println("Player added. Current queue size: " + playerPool.size());
        printQueueStatus();
    }

    public void removePlayerFromQueue(UUID playerId) {
        playerPool.remove(playerId);
        printQueueStatus(); // Print queue status when a player is removed
    }

    // TODO: Implement a proper matchmaking algorithm
    // Temporary method to find a match
    // Once we have a proper matchmaking algorithm, this method will be replaced
    public Match findMatch() {
        if (playerPool.size() < 2) {
            return null;
        }

        PlayerProfile player1 = playerPool.values().iterator().next();
        playerPool.remove(player1.getProfileId());

        PlayerProfile player2 = playerPool.values().iterator().next();
        playerPool.remove(player2.getProfileId());

        Match match = new Match();
        match.setGameType(Match.GameType.SOLO);
        match.setPlayer1Id(player1.getProfileId());
        match.setPlayer2Id(player2.getProfileId());
        match.setStatus(Match.MatchStatus.SCHEDULED);

        return match;
    }

    public void printQueueStatus() {
        System.out.println("Current players in queue: " + playerPool.size());
        for (PlayerProfile player : playerPool.values()) {
            System.out.println("Player: " + player.getFirstName() + " " + player.getLastName() + " (ID: "
                    + player.getProfileId() + ")");
        }
        System.out.println("--------------------");
    }
}