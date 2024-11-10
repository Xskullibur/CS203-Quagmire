package com.project.G1_T3.matchmaking.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.service.MatchService;

@RestController
@RequestMapping("/matches")
public class MatchesController {

    @Autowired
    private MatchService matchService;

    @PutMapping("/{matchId}/forfeit")
    public ResponseEntity<Match> forfeitMatch(@PathVariable UUID matchId, @RequestParam UUID forfeitedById) {
        Match match = matchService.forfeitMatch(matchId, forfeitedById);

        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(match);
    }

    @PutMapping("/{matchId}/complete")
    public ResponseEntity<Match> completeMatch(
            @PathVariable UUID matchId,
            @RequestParam UUID winnerId,
            @RequestParam String score) {
        Match match = matchService.completeMatch(matchId, winnerId, score);

        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(match);
    }

    /**
     * Endpoint to get the current match for a user by their user ID.
     *
     * @param userId the UUID of the user
     * @return ResponseEntity containing the current match if found, otherwise a 404
     *         Not Found status
     */
    @GetMapping("/current/{userId}")
    public ResponseEntity<Match> getCurrentMatch(@PathVariable UUID userId) {
        Match currentMatch = matchService.getCurrentMatchForUserById(userId);
        if (currentMatch != null) {
            return ResponseEntity.ok(currentMatch);
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
