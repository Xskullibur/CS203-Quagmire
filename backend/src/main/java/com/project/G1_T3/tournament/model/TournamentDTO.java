package com.project.G1_T3.tournament.model;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.StageDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private Set<UUID> refereeIds;
    private List<StageDTO> stageDTOs;

    // Getters and setters
}
