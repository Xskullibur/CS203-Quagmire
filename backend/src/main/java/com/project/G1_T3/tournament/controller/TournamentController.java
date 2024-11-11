package com.project.G1_T3.tournament.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.security.validator.RequiresEmailVerification;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.service.TournamentService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    Logger logger = LoggerFactory.getLogger(TournamentController.class);

    // Get all tournaments with pagination
    @GetMapping
    public ResponseEntity<Page<Tournament>> getAllTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.getAllTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    // Get tournaments by name with filtering
    @GetMapping("/search")
    public ResponseEntity<Page<Tournament>> searchByName(@RequestParam("name") String name, Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.searchByName(name, pageable);
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<Tournament>> getUpcomingTournaments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        Page<Tournament> tournaments;
        if (from != null && to != null) {
            tournaments = tournamentService.findTournamentsByAvailability(pageable, from, to);
        } else {
            tournaments = tournamentService.findUpcomingTournaments(pageable);
        }
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Tournament>> getFeaturedTournaments(Pageable pageable) {
        try {
            List<Tournament> tournaments = tournamentService.findFeaturedTournaments(pageable);
            return ResponseEntity.ok(tournaments);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get past tournaments with pagination
    @GetMapping("/past")
    public ResponseEntity<Page<Tournament>> getPastTournaments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        Page<Tournament> tournaments;
        if (from != null && to != null) {
            tournaments = tournamentService.findPastTournamentsWithinDateRange(from, to, pageable);
        } else {
            tournaments = tournamentService.findPastTournaments(pageable);
        }
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/current")
    public ResponseEntity<Page<Tournament>> getCurrentTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.findCurrentTournaments(pageable);
        return ResponseEntity.ok(tournaments);
    }

    // Get tournament DTO by ID
    @GetMapping("/DTO/{id}")
    public ResponseEntity<TournamentDTO> getTournamentDTO(@PathVariable UUID id) {
        TournamentDTO tournament = tournamentService.findTournamentDTO(id);
        if (tournament == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tournament);
    }

    // Get tournament by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable UUID id) {
        Tournament tournament = tournamentService.findTournamentById(id);
        if (tournament == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tournament);
    }

    // Create a new tournament
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> createTournament(
            @RequestPart("tournament") String tournamentJson, // JSON as a String
            @RequestPart(value = "photo", required = false) MultipartFile photo // Optional photo file
    ) {
        try {
            // Log to check if the request is received
            System.out.println("Received request to create tournament with data: " + tournamentJson);

            // Initialize ObjectMapper with JavaTimeModule
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert JSON string to TournamentDTO
            TournamentDTO tournamentDTO = objectMapper.readValue(tournamentJson, TournamentDTO.class);

            // Call the service to create the tournament
            Tournament createdTournament = tournamentService.createTournament(tournamentDTO, photo);

            // Log successful creation
            System.out.println("Tournament created successfully: " + createdTournament.getName());

            // Return response
            return ResponseEntity.ok(createdTournament);
        } catch (Exception e) {
            // Log any error that occurs
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating tournament: " + e.getStackTrace() + ", \nmessage: " + e.getMessage());
        }
    }

    // Update an existing tournament
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(
            @PathVariable UUID id, @RequestBody Tournament updatedTournament) {
        Tournament tournament = tournamentService.updateTournament(id, updatedTournament);
        return ResponseEntity.ok(tournament);
    }

    // Add a player to a tournament
    @RequiresEmailVerification
    @PutMapping("/{tournamentId}/players/{playerId}")
    public ResponseEntity<Tournament> addPlayerToTournament(
            @PathVariable UUID tournamentId, @PathVariable UUID playerId) {
        Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, playerId);
        return ResponseEntity.ok(updatedTournament);
    }

    @DeleteMapping("/{tournamentId}/players/{playerId}")
    public ResponseEntity<Tournament> deletePlayerFromTournament(
            @PathVariable UUID tournamentId, @PathVariable UUID playerId) {
        Tournament updatedTournament = tournamentService.deletePlayerFromTournament(tournamentId, playerId);
        return ResponseEntity.ok(updatedTournament);
    }

    // Get all players in a tournament
    @GetMapping("/{tournamentId}/players")
    public ResponseEntity<Set<PlayerProfile>> getPlayersInTournament(@PathVariable UUID tournamentId) {
        Set<PlayerProfile> players = tournamentService.getPlayers(tournamentId);
        return ResponseEntity.ok(players);
    }

    // Start tournament
    @PutMapping("/{tournamentId}/start")
    public ResponseEntity<String> startTournament(@PathVariable UUID tournamentId) {
        try {
            // Call the service method to start the tournament
            Tournament createdTournament = tournamentService.startTournament(tournamentId);
            return ResponseEntity.ok(createdTournament.getId().toString());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tournament not found: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid tournament state: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error starting tournament: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start tournament: " + e.getMessage());
        }
    }

    // Progress tournament (includes ending)
    @PutMapping("/{tournamentId}/progress")
    public ResponseEntity<String> progressToNextStage(@PathVariable UUID tournamentId) {
        try {
            // Call the service method to progress to the next stage
            tournamentService.progressToNextStage(tournamentId);
            return ResponseEntity.ok("Tournament progressed to the next stage successfully.");
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while progressing to the next stage.");
        }
    }

}
