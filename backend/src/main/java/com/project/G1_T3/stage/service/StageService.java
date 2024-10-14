package com.project.G1_T3.stage.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.tournament.model.Tournament;


import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface StageService {

    // Save a new stage
    Stage saveStage(Stage stage);

    // Find all stages for a specific tournament
    List<Stage> findAllStagesByTournamentId(Long tournamentId);

    // Find a specific stage by stageId and tournamentId
    Stage findStageByIdAndTournamentId(UUID stageId, Long tournamentId);

    // Update a stage for a specific tournament
    Stage updateStageForTournament(Long tournamentId, UUID stageId, Stage updatedStage);

    // Delete a stage by stageId and tournamentId
    void deleteStageByTournamentId(Long tournamentId, UUID stageId);

    public void startStage(UUID stageId);
    
    public void createStage(StageDTO stageDTO, Tournament tournament);

}
