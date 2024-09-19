package com.project.G1_T3.matchmaking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column(name = "player1_id")
    private UUID player1Id;

    @Column(name = "player2_id")
    private UUID player2Id;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "winner_id")
    private UUID winnerId;

    private String score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "meetingLatitude")
    private double meetingLatitude;

    @Column(name = "meetingLongitude")
    private double meetingLongitude;

    public enum GameType {
        SOLO, TOURNAMENT
    }

    public enum MatchStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Constructors, getters, and setters

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public UUID getPlayer1Id() {
        return player1Id;
    }

    public UUID getPlayer2Id() {
        return player2Id;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public void setPlayer1Id(UUID player1Id) {
        this.player1Id = player1Id;
    }

    public void setPlayer2Id(UUID player2Id) {
        this.player2Id = player2Id;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public GameType getGameType() {
        return gameType;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setMeetingLatitude(double meetingLatitude) {
        this.meetingLatitude = meetingLatitude;
    }

    public void setMeetingLongitude(double meetingLongitude) {
        this.meetingLongitude = meetingLongitude;
    }

    public double getMeetingLatitude() {
        return meetingLatitude;
    }

    public double getMeetingLongitude() {
        return meetingLongitude;
    }

}