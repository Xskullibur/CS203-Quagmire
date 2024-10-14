package com.project.G1_T3.stage.model;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StageDTO {

    private UUID stageId;
    private String stageName;
    private Integer stageNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<PlayerProfile> players;
    private Set<PlayerProfile> referees;
    private Status status;  // Default to UPCOMING for creation
    private Format format;   // SINGLE_ELIMINATION, DOUBLE_ELIMINATION, etc.
}
