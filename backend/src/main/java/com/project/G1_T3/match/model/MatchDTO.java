package com.project.G1_T3.match.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class MatchDTO {
    private UUID player1Id;
    private UUID player2Id;
    private LocalDateTime scheduledTime;
    private UUID winnerId;
    private String score;
    private Double meetingLatitude;
    private Double meetingLongitude;

}