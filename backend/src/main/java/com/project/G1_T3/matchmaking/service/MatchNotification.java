package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;

public class MatchNotification {
    private Match match;
    private String opponentName;

    public MatchNotification(Match match, String opponentName) {
        this.match = match;
        this.opponentName = opponentName;
    }

    // Getters and setters

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }
}