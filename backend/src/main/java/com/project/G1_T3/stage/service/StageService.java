package com.project.G1_T3.stage.service;

import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.tournament.model.Tournament;


import java.util.List;
import java.util.UUID;

public interface StageService {

    // Save a new stage
    Stage saveStage(Stage stage);

    public Stage getStageById(UUID stageId);

    // Find all stages for a specific tournament
    List<Stage> findAllStagesByTournamentIdSortedByCreatedAtAsc(UUID tournamentId);

    // Find a specific stage by stageId and tournamentId
    // Stage findStageByIdAndTournamentId(UUID stageId, UUID tournamentId);

    // Update a stage for a specific tournament
    Stage updateStageForTournament(UUID tournamentId, UUID stageId, Stage updatedStage);

    // Delete a stage by stageId and tournamentId
    void deleteStageByTournamentId(UUID tournamentId, UUID stageId);

    public void startStage(UUID stageId);
    
    public Stage createStage(StageDTO stageDTO, Tournament tournament);

}
