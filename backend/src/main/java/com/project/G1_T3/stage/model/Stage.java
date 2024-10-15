package com.project.G1_T3.stage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.G1_T3.tournament.model.Tournament;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.round.model.Round;


@Entity
@Getter
@Setter
@Table(name = "stages")
public class Stage {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue
    private UUID stageId;

    @ManyToOne
    @JoinColumn(name = "tournament", nullable = false)
    @JsonIgnore
    private Tournament tournament;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "start_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Format format;

    @Column
    private UUID winnerId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "stage_players",  // Create a join table for players in each stage
        joinColumns = @JoinColumn(name = "stage_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<PlayerProfile> players;

    @ManyToMany
    @JoinTable(
        name = "stage_referees",  // Create a join table for referees in each stage
        joinColumns = @JoinColumn(name = "stage_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<PlayerProfile> referees;

    @ManyToMany
    @JoinTable(
        name = "progressing_players",  // Create a join table for players in each stage
        joinColumns = @JoinColumn(name = "stage_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<PlayerProfile> progressingPlayers;

    @OneToMany(mappedBy = "stage")
    private List<Round> rounds;

}
