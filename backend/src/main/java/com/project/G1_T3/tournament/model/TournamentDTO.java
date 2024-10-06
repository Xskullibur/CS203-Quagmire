package com.project.G1_T3.tournament.model;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TournamentDTO {

    private String name;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime deadline;
    private String description;
    private Status status;
    private Set<PlayerProfile> players;
    private List<Stage> stages;  

    // Getters and setters
}
