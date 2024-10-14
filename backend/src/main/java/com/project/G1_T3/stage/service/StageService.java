package com.project.G1_T3.stage.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.tournament.model.Tournament;


import java.util.List;
import java.util.Set;

public interface StageService {

    // Save a new stage
    Stage saveStage(Stage stage);

    // Find all stages for a specific tournament
    List<Stage> findAllStagesByTournamentId(Long tournamentId);

    // Find a specific stage by stageId and tournamentId
    Stage findStageByIdAndTournamentId(Long stageId, Long tournamentId);

    // Update a stage for a specific tournament
    Stage updateStageForTournament(Long tournamentId, Long stageId, Stage updatedStage);

    // Delete a stage by stageId and tournamentId
    void deleteStageByTournamentId(Long tournamentId, Long stageId);

    public void startStage(Long stageId);
    
    public void createStage(StageDTO stageDTO, Tournament tournament);

}
