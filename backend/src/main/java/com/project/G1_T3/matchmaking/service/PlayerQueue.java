package com.project.G1_T3.matchmaking.service;

import java.util.List;
import java.util.UUID;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.playerprofile.model.PlayerProfile;

public interface PlayerQueue {
    void addPlayer(PlayerProfile player, double latitude, double longitude);

    QueuedPlayer pollPlayer();

    int size();

    boolean containsPlayer(UUID userId);

    void removePlayer(UUID playerId);

    QueuedPlayer findMatch(QueuedPlayer player);

    List<QueuedPlayer> getAllPlayers();

}