package com.project.G1_T3.match.model;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.round.model.Round;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = true)
    private Round round;

    @Column(nullable = false)
    private UUID player1Id;

    @Column(nullable = false)
    private UUID player2Id;

    @Column(nullable = true)
    private UUID refereeId;

    @Column(name = "scheduled_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status; 

    @Column
    private UUID winnerId;

    @Column(length = 50)
    private String score;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public void startMatch() {
        if (this.status == Status.SCHEDULED) {
            this.status = Status.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void completeMatch(UUID winnerId, String score) {
        if (this.status == Status.IN_PROGRESS) {
            this.winnerId = winnerId;
            this.score = score;
            this.status = Status.COMPLETED;
            this.updatedAt = LocalDateTime.now();
        }
    }

}
