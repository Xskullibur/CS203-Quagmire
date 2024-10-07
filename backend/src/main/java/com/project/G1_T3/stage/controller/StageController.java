package com.project.G1_T3.stage.controller;

import com.project.G1_T3.round.service.RoundService;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.service.TournamentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament/{tournamentId}/stage")
public class StageController {

    @Autowired
    private StageService stageService;

    // Endpoint to start a stage by its ID
    @PutMapping("/{stageId}/start")
    public ResponseEntity<String> startStage(@PathVariable Long stageId) {
        try {
            stageService.startStage(stageId);  // Call the service method to start the stage
            return ResponseEntity.ok("Stage started successfully");
        } catch (RuntimeException e) {
            // Handle any exceptions that may occur (e.g., stage not found or invalid status)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

