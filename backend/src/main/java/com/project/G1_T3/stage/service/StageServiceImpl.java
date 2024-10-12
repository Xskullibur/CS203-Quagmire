package com.project.G1_T3.stage.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.round.service.RoundService;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.repository.StageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StageServiceImpl implements StageService {

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private RoundService roundService;

    @Autowired RoundRepository roundRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    // Save a new stage
    public Stage saveStage(Stage stage) {
        return stageRepository.save(stage);
    }

    // Find all stages for a specific tournament
    public List<Stage> findAllStagesByTournamentId(UUID tournamentId) {
        return stageRepository.findByTournamentId(tournamentId);
    }

    // Find a specific stage by stageId and tournamentId
    public Stage findStageByIdAndTournamentId(Long stageId, UUID tournamentId) {
        return stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));
    }

    // Update a stage for a specific tournament
    public Stage updateStageForTournament(UUID tournamentId, Long stageId, Stage updatedStage) {
        Stage stage = findStageByIdAndTournamentId(stageId, tournamentId);
        stage.setStageName(updatedStage.getStageName());
        stage.setStartDate(updatedStage.getStartDate());
        stage.setEndDate(updatedStage.getEndDate());
        stage.setStatus(updatedStage.getStatus());
        stage.setFormat(updatedStage.getFormat());
        return stageRepository.save(stage);
    }

    // Delete a stage by stageId and tournamentId
    public void deleteStageByTournamentId(UUID tournamentId, Long stageId) {
        Stage stage = findStageByIdAndTournamentId(stageId, tournamentId);
        stageRepository.delete(stage);
    }


    // Method to start a stage and initialize the first round
    public void startStage(Long stageId) {
        // Find the stage by ID
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        // Check if the stage is eligible to start (i.e., it has not already started)
        // if (stage.getStatus() != Status.UPCOMING) {
        //     throw new RuntimeException("Stage is not in an upcoming state and cannot be started.");
        // }

        Set<PlayerProfile> players = stage.getPlayers();
        List<PlayerProfile> sortedPlayers = new ArrayList<>(players);
        Collections.sort(sortedPlayers, (a, b) -> Double.compare(b.getCurrentRating(), a.getCurrentRating()));

        System.out.println("RATINGS");
        for (int i = 0; i < sortedPlayers.size(); i++) {
            System.out.print(sortedPlayers.get(i).getCurrentRating());
        }
        System.out.println();

        roundService.createFirstRound(stageId, sortedPlayers);

        // Mark the stage as in-progress
        stage.setStatus(Status.IN_PROGRESS);
        stageRepository.save(stage);

    }

}
