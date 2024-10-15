package com.project.G1_T3.match.controller;

import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchDTO matchDTO) {
        matchService.createMatch(matchDTO);
        return ResponseEntity.ok("Match created");
    }

    // // Referee starts a match
    // @PutMapping("/{matchId}/start")
    // public ResponseEntity<?> startMatch(@PathVariable Long matchId, @RequestParam UUID refereeId) {
    //     matchService.startMatch(matchId, refereeId);
    //     return ResponseEntity.ok("Match started");
    // }

    // Referee starts a match
    @PutMapping("/{matchId}/start")
    public ResponseEntity<?> startMatch(@PathVariable UUID matchId, @RequestBody MatchDTO matchDTO) {
        matchService.startMatch(matchId, matchDTO);
        return ResponseEntity.ok("Match started");
    }

    // Referee completes a match and selects a winner
    @PutMapping("/{matchId}/complete")
    public ResponseEntity<?> completeMatch(@PathVariable UUID matchId, @RequestBody MatchDTO matchDTO) {
        matchService.completeMatch(matchId, matchDTO);
        return ResponseEntity.ok("Match completed");
    }
}
