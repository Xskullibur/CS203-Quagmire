package com.project.G1_T3.matchmaking.model;

import com.project.G1_T3.player.model.PlayerProfile;

import lombok.NoArgsConstructor;

import java.util.UUID;

import com.project.G1_T3.match.model.Match;

@NoArgsConstructor

public class MatchNotification {
    private Match match;
    private String opponentName;
    private double meetingLatitude;
    private double meetingLongitude;
    private PlayerProfile opponentProfile;

    public MatchNotification(Match match, String opponentName, PlayerProfile opponentProfile) {
        this.match = match;
        this.opponentName = opponentName;
        this.meetingLatitude = match.getMeetingLatitude();
        this.meetingLongitude = match.getMeetingLongitude();
        this.opponentProfile = opponentProfile;
    }
    // Getters and setters

    public UUID getMatchId() {
        return match.getMatchId();
    }

    public Match getMatch() {
        return match;
    }

    // Setter
    public void setMatch(Match match) {
        this.match = match;
    }
}