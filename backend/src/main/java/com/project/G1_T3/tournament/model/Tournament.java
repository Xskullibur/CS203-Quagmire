package com.project.G1_T3.tournament.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @Column(name = "deadline", columnDefinition = "TIMESTAMP")
    private LocalDateTime deadline;

    // @Column(nullable = false)
    @Column
    private Integer maxParticipants;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status;

    @Column
    private UUID winnerId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tournament_players",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    @JsonIgnore
    private Set<PlayerProfile> players;

    @ManyToMany
    @JoinTable(
        name = "tournament_referees",  // Create a join table for referees in each stage
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    @JsonIgnore
    private Set<PlayerProfile> referees;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stage> stages = new ArrayList<>();  // Add a list to hold the stage

    @Column
    private int numStages;

    @Column
    private int currentStageIndex = 0;

}
