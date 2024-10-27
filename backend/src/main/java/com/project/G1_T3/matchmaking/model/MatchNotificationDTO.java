package com.project.G1_T3.matchmaking.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;

public class MatchNotificationDTO {
    @JsonProperty("matchId")
    private UUID matchId;

    @JsonProperty("meetingLatitude")
    private double meetingLatitude;

    @JsonProperty("meetingLongitude")
    private double meetingLongitude;

    @JsonProperty("opponentName")
    private String opponentName;

    @JsonProperty("opponentProfile")
    private PlayerProfileDTO opponentProfile;

    public MatchNotificationDTO() {
    }

    public MatchNotificationDTO(UUID matchId, double meetingLatitude, double meetingLongitude, String opponentName,
            PlayerProfileDTO opponentProfile) {
        this.matchId = matchId;
        this.meetingLatitude = meetingLatitude;
        this.meetingLongitude = meetingLongitude;
        this.opponentName = opponentName;
        this.opponentProfile = opponentProfile;
    }

    // Add getters and setters for all fields
    // ...
}