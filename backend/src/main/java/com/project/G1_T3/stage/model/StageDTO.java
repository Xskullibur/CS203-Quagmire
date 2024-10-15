package com.project.G1_T3.stage.model;

import com.project.G1_T3.common.model.Status;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StageDTO {

    private Long stageId;
    private String stageName;
    private Integer stageNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;  // Default to UPCOMING for creation
    private Format format;   // SINGLE_ELIMINATION, DOUBLE_ELIMINATION, etc.
}
