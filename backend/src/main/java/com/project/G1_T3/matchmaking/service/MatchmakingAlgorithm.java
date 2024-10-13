package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;

public interface MatchmakingAlgorithm {
    boolean isGoodMatch(QueuedPlayer player1, QueuedPlayer player2);
}