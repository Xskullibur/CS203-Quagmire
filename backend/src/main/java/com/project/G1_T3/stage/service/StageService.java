package com.project.G1_T3.stage.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;

import java.util.*;

public interface StageService {

    // Save a new stage
    Stage saveStage(Stage stage);

    // Find all stages for a specific tournament
    List<Stage> findAllStagesByTournamentId(UUID tournamentId);

    // Find a specific stage by stageId and tournamentId
    Stage findStageByIdAndTournamentId(Long stageId, UUID tournamentId);

    // Update a stage for a specific tournament
    Stage updateStageForTournament(UUID tournamentId, Long stageId, Stage updatedStage);

    // Delete a stage by stageId and tournamentId
    void deleteStageByTournamentId(UUID tournamentId, Long stageId);

    public void startStage(Long stageId);

}
