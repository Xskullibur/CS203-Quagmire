package com.project.G1_T3.match.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MatchDTO {
    private UUID player1Id;
    private UUID player2Id;
    private UUID refereeId;
    private LocalDateTime scheduledTime;
    private UUID winnerId;
    private String score;
}
