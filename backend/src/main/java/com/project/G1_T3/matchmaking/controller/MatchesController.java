package com.project.G1_T3.matchmaking.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.service.MatchService;

@RestController
@RequestMapping("/matches")
public class MatchesController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/current/{userId}")
    public ResponseEntity<Match> getCurrentMatch(@PathVariable UUID userId) {
        Match currentMatch = matchService.getCurrentMatchForUserById(userId);
        if (currentMatch != null) {
            return ResponseEntity.ok(currentMatch);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
