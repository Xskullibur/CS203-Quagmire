package com.project.G1_T3.matchmaking.model;

import java.util.UUID;

import com.project.G1_T3.player.model.PlayerProfileDTO;

public class MatchNotificationDTO {
    private UUID matchId;
    private double meetingLatitude;
    private double meetingLongitude;
    private String opponentName;
    private PlayerProfileDTO opponentProfile;

    public MatchNotificationDTO(UUID matchId, double meetingLatitude, double meetingLongitude, String opponentName,
            PlayerProfileDTO opponentProfile) {
        this.matchId = matchId;
        this.meetingLatitude = meetingLatitude;
        this.meetingLongitude = meetingLongitude;
        this.opponentName = opponentName;
        this.opponentProfile = opponentProfile;
    }

    // Constructor, getters, and setters
}
