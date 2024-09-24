package com.project.G1_T3.match.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.project.G1_T3.player.model.PlayerProfile;

@Entity
@Table(name = "match_results")
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id", nullable = false)
    private PlayerProfile winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loser_id", nullable = false)
    private PlayerProfile loser;

    @Column(name = "result_date", nullable = false)
    private LocalDateTime resultDate;

    @Column(name = "score", nullable = true)
    private String score;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public PlayerProfile getWinner() {
        return winner;
    }

    public void setWinner(PlayerProfile winner) {
        this.winner = winner;
    }

    public PlayerProfile getLoser() {
        return loser;
    }

    public void setLoser(PlayerProfile loser) {
        this.loser = loser;
    }

    public LocalDateTime getResultDate() {
        return resultDate;
    }

    public void setResultDate(LocalDateTime resultDate) {
        this.resultDate = resultDate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
