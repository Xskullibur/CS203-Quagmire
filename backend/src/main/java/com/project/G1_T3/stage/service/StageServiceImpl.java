package com.project.G1_T3.stage.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.round.service.RoundService;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.tournament.model.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
public class StageServiceImpl implements StageService {

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private RoundService roundService;

    // Save a new stage
    public Stage saveStage(Stage stage) {
        return stageRepository.save(stage);
    }

    public Stage getStageById(UUID stageId) {
        // Find the stage by stageId, or throw an exception if not found
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage not found with id: " + stageId));
    }

    // Find all stages for a specific tournament
    public List<Stage> findAllStagesByTournamentIdSortedByCreatedAtAsc(UUID tournamentId) {
        return stageRepository.findByTournamentIdOrderByCreatedAtAsc(tournamentId);
    }

    // Find a specific stage by stageId and tournamentId
    // public Stage findStageByIdAndTournamentId(UUID stageId, UUID tournamentId) {
    //     return stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)
    //             .orElseThrow(() -> new RuntimeException("Stage not found"));
    // }

    // Update a stage for a specific tournament
    public Stage updateStageForTournament(UUID tournamentId, UUID stageId, Stage updatedStage) {
        Stage stage = getStageById(stageId);
        stage.setStageName(updatedStage.getStageName());
        stage.setStartDate(updatedStage.getStartDate());
        stage.setEndDate(updatedStage.getEndDate());
        stage.setStatus(updatedStage.getStatus());
        stage.setFormat(updatedStage.getFormat());
        return stageRepository.save(stage);
    }

    // Delete a stage by stageId and tournamentId
    public void deleteStageByTournamentId(UUID tournamentId, UUID stageId) {
        Stage stage = getStageById(stageId);
        stageRepository.delete(stage);
    }


    // Method to start a stage and initialize the first round
    public void startStage(UUID stageId) {
        // Validate input
        if (stageId == null) {
            throw new IllegalArgumentException("Stage ID must not be null");
        }
    
        // Find the stage by ID
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));
    
        // Check if the stage is eligible to start (i.e., it has not already started)
        if (stage.getStatus() != Status.SCHEDULED) {
            throw new IllegalStateException("Stage is not in a scheduled state and cannot be started.");
        }
    
        // Ensure there are players in the stage
        Set<PlayerProfile> players = stage.getPlayers();
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("There are no players in this stage. Cannot start the stage.");
        }
    
        // Sort players by rating
        List<PlayerProfile> sortedPlayers = new ArrayList<>(players);
        Collections.sort(sortedPlayers, (a, b) -> Double.compare(b.getCurrentRating(), a.getCurrentRating()));
    
        // Create the first round with sorted players
        try {
            roundService.createFirstRound(stageId, sortedPlayers);
        } catch (Exception e) {
            throw new RuntimeException("Error creating the first round: " + e.getMessage(), e);
        }
    
        // Mark the stage as in-progress
        stage.setStatus(Status.IN_PROGRESS);
    
        // Save the updated stage
        try {
            stageRepository.save(stage);
        } catch (Exception e) {
            throw new RuntimeException("Error saving the stage: " + e.getMessage(), e);
        }
    }
    

    public Stage createStage(StageDTO stageDTO, Tournament tournament) {

        // Validate input: Tournament must not be null
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament field is null");
        }
    
        // Validate input: Stage name must not be null or empty
        if (stageDTO.getStageName() == null || stageDTO.getStageName().isEmpty()) {
            throw new IllegalArgumentException("Stage name is required");
        }
    
        // Validate input: Start date must not be null
        if (stageDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
    
        // Validate input: End date must not be null
        if (stageDTO.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
    
        // Validate input: End date must not be before start date
        if (stageDTO.getEndDate().isBefore(stageDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    
        // // Validate input: Players must not be null and should be more than 1
        // if (stageDTO.getPlayers() == null || stageDTO.getPlayers().size() <= 1) {
        //     throw new IllegalArgumentException("There must be more than 1 player");
        // }
    
        // Validate input: Referees must not be null and should have at least 1 referee
        // if (stageDTO.getReferees() == null || stageDTO.getReferees().isEmpty()) {
        //     throw new IllegalArgumentException("There must be at least 1 referee");
        // }
    
        // Create a new Stage entity and populate its fields
        Stage stage = new Stage();
        try {
            stage.setStageId(UUID.randomUUID()); // Generate a new UUID for the stage
            stage.setStageName(stageDTO.getStageName());
            stage.setStartDate(stageDTO.getStartDate());
            stage.setEndDate(stageDTO.getEndDate());
            stage.setFormat(stageDTO.getFormat() != null ? stageDTO.getFormat() : Format.SINGLE_ELIMINATION); // Default format
            stage.setStatus(stageDTO.getStatus() != null ? stageDTO.getStatus() : Status.SCHEDULED);  // Default to SCHEDULED
            stage.setTournament(tournament);  // Set the associated tournament
            // stage.setPlayers(stageDTO.getPlayers());
            // stage.setReferees(stageDTO.getReferees());
    
            // Save the stage in the repository
            stage = stageRepository.save(stage);
        } catch (Exception e) {
            throw new RuntimeException("Error saving Stage: " + e.getMessage(), e);  // Add root cause
        }
    
        System.out.println("Stage created successfully");

        return stage;
    }

}
