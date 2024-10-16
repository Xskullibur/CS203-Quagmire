package com.project.G1_T3.matchmaking.model;

public class QueueRequest {
    private String playerId;
    private MatchLocation location;

    // Getters and setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public MatchLocation getLocation() {
        return location;
    }

    public void setLocation(MatchLocation location) {
        this.location = location;
    }
}