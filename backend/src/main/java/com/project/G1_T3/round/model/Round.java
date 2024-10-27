package com.project.G1_T3.round.model;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.G1_T3.common.model.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "rounds")
public class Round {
    
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue()
    private UUID roundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_stage", nullable = false)
    @JsonIgnore
    private Stage stage;

    @Column(nullable = false)
    private Integer roundNumber;

    @Column(name = "start_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status;  // Define this enum with values: UPCOMING, IN_PROGRESS, COMPLETED

    // @Enumerated(EnumType.STRING)
    // @Column(length = 50, nullable = false)
    // private Format format;

    // @OneToMany(mappedBy = "round", fetch = FetchType.EAGER)
    // @JoinColumn(name = "round_id", nullable = false)
    // private List<Match> matches;

    @OneToMany
    @JoinColumn(name = "round_id")  // This tells Hibernate to use `round_id` as the FK in `matches`
    private List<Match> matches = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "round_players",  // Junction table for players in rounds
        joinColumns = @JoinColumn(name = "round_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<PlayerProfile> players;  // Players participating in this round

    @ManyToMany
    @JoinTable(
        name = "round_referees",  // Create a join table for referees in each stage
        joinColumns = @JoinColumn(name = "round_id"),
        inverseJoinColumns = @JoinColumn(name = "referee_id")
    )
    private Set<PlayerProfile> referees;

    public void startRound() {
        if (this.status == Status.SCHEDULED) {
            this.status = Status.IN_PROGRESS;
            this.startDate = LocalDateTime.now();
        }
    }

    public void endRound() {
        if (this.status == Status.IN_PROGRESS) {
            this.status = Status.COMPLETED;
            this.endDate = LocalDateTime.now();
        }
    }

}
