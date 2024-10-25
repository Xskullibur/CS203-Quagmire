package com.project.G1_T3.round.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.service.RoundService;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/tournament/{tournamentId}/stage/{stageId}/round")
public class RoundController {

    @Autowired
    private RoundService roundService;

    // Retrieve a round by its roundId
    @GetMapping("/{roundId}")
    public ResponseEntity<Round> getRoundById(@PathVariable UUID roundId) {
        Round round = roundService.getRoundById(roundId);
        return ResponseEntity.ok(round);
    }

    // Retrieve a list of rounds by stageId, sorted by roundNumber
    @GetMapping("/allRounds")
    public ResponseEntity<List<Round>> getRoundsByStageId(@PathVariable UUID stageId) {
        List<Round> rounds = roundService.getRoundsByStageId(stageId);
        return ResponseEntity.ok(rounds);
    }

    @PutMapping("/{roundId}/end")
    public ResponseEntity<String> endRound(@PathVariable UUID roundId) {
        try {
            roundService.endRound(roundId);  // Call the service method to end the round
            return ResponseEntity.ok("Round ended successfully");
        } catch (RuntimeException e) {
            // Handle any exceptions (e.g., round not found)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
