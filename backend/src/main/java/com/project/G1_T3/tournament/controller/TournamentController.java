package com.project.G1_T3.tournament.controller;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Get all tournaments with pagination
    @GetMapping
    public ResponseEntity<Page<Tournament>> getAllTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.getAllTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    // Get tournaments by name with filtering
    @GetMapping("/search")
    public ResponseEntity<List<Tournament>> searchByName(@RequestParam("name") String name) {
        List<Tournament> tournaments = tournamentService.searchByName(name);
        return ResponseEntity.ok(tournaments);
    }

    // Get upcoming tournaments with pagination
    @GetMapping("/upcoming")
    public ResponseEntity<Page<Tournament>> getUpcomingTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.findUpcomingTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    // Get past tournaments with pagination
    @GetMapping("/past")
    public ResponseEntity<Page<Tournament>> getPastTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.findPastTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    // Get tournament by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tournament);
    }

    // Create a new tournament
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        Tournament createdTournament = tournamentService.createTournament(tournament);
        return ResponseEntity.ok(createdTournament);
    }

    // Update an existing tournament
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(
            @PathVariable Long id, @RequestBody Tournament updatedTournament) {
        Tournament tournament = tournamentService.updateTournament(id, updatedTournament);
        return ResponseEntity.ok(tournament);
    }

    // Add a player to a tournament
    @PostMapping("/{tournamentId}/players/{playerId}")
    public ResponseEntity<Tournament> addPlayerToTournament(
            @PathVariable Long tournamentId, @PathVariable UUID playerId) {
        Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, playerId);
        return ResponseEntity.ok(updatedTournament);
    }

    // Get all players in a tournament
    @GetMapping("/{tournamentId}/players")
    public ResponseEntity<Set<PlayerProfile>> getPlayersInTournament(@PathVariable Long tournamentId) {
        Set<PlayerProfile> players = tournamentService.getPlayers(tournamentId);
        return ResponseEntity.ok(players);
    }

    
}
