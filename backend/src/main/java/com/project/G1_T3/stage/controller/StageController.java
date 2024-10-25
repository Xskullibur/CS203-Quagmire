package com.project.G1_T3.stage.controller;

import com.project.G1_T3.round.service.RoundService;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.service.TournamentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/stage")
public class StageController {

    @Autowired
    private StageService stageService;

    @GetMapping("/{stageId}")
    public ResponseEntity<Stage> getStageById(@PathVariable UUID stageId) {
        // Call the service to retrieve the stage by its stageId
        Stage stage = stageService.getStageById(stageId);
        return ResponseEntity.ok(stage);
    }

    // Endpoint to start a stage by its ID
    @PutMapping("/{stageId}/start")
    public ResponseEntity<String> startStage(@PathVariable UUID stageId) {
        try {
            stageService.startStage(stageId);  // Call the service method to start the stage
            return ResponseEntity.ok("Stage started successfully");
        } catch (RuntimeException e) {
            // Handle any exceptions that may occur (e.g., stage not found or invalid status)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get stage by ID
    // @GetMapping("/{stageId}")
    // public ResponseEntity<Stage> getStageById(@PathVariable UUID tournamentId, @PathVariable UUID stageId) {
    //     try {
    //         Stage stage = stageService.getStageById(tournamentId, stageId);
    //         if (stage == null) {
    //             return ResponseEntity.notFound().build();
    //         }
    //         return ResponseEntity.ok(stage);
    //     } catch (RuntimeException e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    //     }
    // }

}

