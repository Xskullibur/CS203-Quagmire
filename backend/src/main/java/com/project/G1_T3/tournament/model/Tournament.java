package com.project.G1_T3.tournament.model;

import com.project.G1_T3.player.model.PlayerProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Tournament name cannot be null")
    @Size(min = 5, max = 50)
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Tournament location cannot be null")
    private String location;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    @NotNull(message = "Tournament start date cannot be null")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    @NotNull(message = "Tournament end date cannot be null")
    private LocalDateTime endDate;

    @Column(name = "deadline", columnDefinition = "TIMESTAMP")
    @NotNull(message = "Tournament deadline cannot be null")
    private LocalDateTime deadline;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tournament_participants",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<PlayerProfile> players = new HashSet<>();
}
