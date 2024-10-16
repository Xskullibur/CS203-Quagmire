package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.player.model.PlayerProfile;
import java.util.UUID;

public interface MatchmakingService {
    void addPlayerToQueue(PlayerProfile player, double latitude, double longitude);

    void removePlayerFromQueue(UUID playerId);

    Match findMatch();

    void printQueueStatus();

    boolean isPlayerInQueue(UUID playerId);

    void triggerMatchmaking();

    MatchNotification createMatchNotification(Match match, UUID uuid);
}