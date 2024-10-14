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
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.service.TournamentService;
import com.project.G1_T3.tournament.repository.TournamentRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.TransactionScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Service
@Transactional
public class StageServiceImpl implements StageService {

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private RoundService roundService;

    @Autowired 
    private RoundRepository roundRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    // Save a new stage
    public Stage saveStage(Stage stage) {
        return stageRepository.save(stage);
    }

    // Find all stages for a specific tournament
    public List<Stage> findAllStagesByTournamentId(Long tournamentId) {
        return stageRepository.findByTournamentId(tournamentId);
    }

    // Find a specific stage by stageId and tournamentId
    public Stage findStageByIdAndTournamentId(UUID stageId, Long tournamentId) {
        return stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));
    }

    // Update a stage for a specific tournament
    public Stage updateStageForTournament(Long tournamentId, UUID stageId, Stage updatedStage) {
        Stage stage = findStageByIdAndTournamentId(stageId, tournamentId);
        stage.setStageName(updatedStage.getStageName());
        stage.setStartDate(updatedStage.getStartDate());
        stage.setEndDate(updatedStage.getEndDate());
        stage.setStatus(updatedStage.getStatus());
        stage.setFormat(updatedStage.getFormat());
        return stageRepository.save(stage);
    }

    // Delete a stage by stageId and tournamentId
    public void deleteStageByTournamentId(Long tournamentId, UUID stageId) {
        Stage stage = findStageByIdAndTournamentId(stageId, tournamentId);
        stageRepository.delete(stage);
    }


    // Method to start a stage and initialize the first round
    public void startStage(UUID stageId) {
        // Find the stage by ID
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        // Check if the stage is eligible to start (i.e., it has not already started)
        // if (stage.getStatus() != Status.SCHEDULED) {
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

    public void createStage(StageDTO stageDTO, Tournament tournament) {

        // Tournament tournament = tournamentRepository.findById(tournamentId)
        //         .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + tournamentId + " not found"));

        if (tournament == null) {
            throw new IllegalArgumentException("Tournament field is null");
        }

        if (stageDTO.getStageName() == null || stageDTO.getStageName().isEmpty()) {
            throw new IllegalArgumentException("Stage name is required");
        }

        if (stageDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (stageDTO.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        if (stageDTO.getEndDate().isBefore(stageDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (stageDTO.getPlayers() == null || stageDTO.getPlayers().size() <= 1) {
            throw new IllegalArgumentException("There must be more than 1 player");
        }

        if (stageDTO.getReferees() == null || stageDTO.getReferees().isEmpty()) {
            throw new IllegalArgumentException("There must be at least 1 referee");
        }

        Stage stage = new Stage();
        // stage.setStageId(); // idk why the thing fails, shouldn't this be autogenerated
        stage.setStageName(stageDTO.getStageName());
        stage.setStartDate(stageDTO.getStartDate());
        stage.setEndDate(stageDTO.getEndDate());
        stage.setFormat(stageDTO.getFormat() != null ? stageDTO.getFormat() : Format.SINGLE_ELIMINATION); // Default format
        stage.setStatus(stageDTO.getStatus() != null ? stageDTO.getStatus() : Status.SCHEDULED);  // Default to UPCOMING
        stage.setTournament(tournament);  // Set the tournament
        stage.setPlayers(stageDTO.getPlayers());
        stage.setReferees(stageDTO.getReferees());

        System.out.println("test3");

        try {
            stage = stageRepository.save(stage);
        } catch (Exception e) {
            throw new RuntimeException("Error saving Stage: " + e.getMessage());
        }

        System.out.println("stage created");
    }

}
