package com.project.G1_T3.tournament.model;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.StageDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.stream.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentDTO {

    private String name;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime deadline;
    private String description;
    private Status status;
    private Integer maxParticipants;
    private Set<UUID> refereeIds;
    private List<StageDTO> stageDTOs;

    public TournamentDTO(Tournament t) {
        name = t.getName();
        location = t.getLocation();
        startDate = t.getStartDate();
        endDate = t.getEndDate();
        deadline = t.getDeadline();
        description = t.getDescription();
        status = t.getStatus();
        maxParticipants = t.getMaxParticipants();
        // Use the correct method reference for UUID mapping
        refereeIds = t.getReferees().stream().map(PlayerProfile::getProfileId).collect(Collectors.toSet());

        // Assuming that `getStages()` returns a list of Stage objects, and you need to
        // map them to StageDTO
    }

    // Getters and setters
}
