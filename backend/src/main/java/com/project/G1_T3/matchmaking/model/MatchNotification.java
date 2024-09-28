package com.project.G1_T3.matchmaking.model;

import com.project.G1_T3.player.model.PlayerProfile;

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

    public double getMeetingLatitude() {
        return meetingLatitude;
    }

    public void setMeetingLatitude(double meetingLatitude) {
        this.meetingLatitude = meetingLatitude;
    }

    public double getMeetingLongitude() {
        return meetingLongitude;
    }

    public void setMeetingLongitude(double meetingLongitude) {
        this.meetingLongitude = meetingLongitude;
    }

    public PlayerProfile getOpponentProfile() {
        return opponentProfile;
    }

    public void setOpponentProfile(PlayerProfile opponentProfile) {
        this.opponentProfile = opponentProfile;
    }
}