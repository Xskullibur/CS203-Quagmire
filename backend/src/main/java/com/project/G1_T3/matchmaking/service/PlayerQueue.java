package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;

public interface PlayerQueue {
    void addPlayer(PlayerProfile player, double latitude, double longitude);

    PlayerProfile pollPlayer();

    int size();
}