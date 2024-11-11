package com.project.G1_T3.tournament.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.service.TournamentService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Page<Tournament>> searchByName(@RequestParam("name") String name, Pageable pageable) {
        Page<Tournament> tournaments = tournamentService.searchByName(name, pageable);
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
    // @PostMapping("/create")
    // public ResponseEntity<Tournament> createTournament(@RequestBody TournamentDTO tournamentDTO) {

    //     System.out.println("test2");

    //     Tournament createdTournament = tournamentService.createTournament(tournamentDTO);
    //     return ResponseEntity.ok(createdTournament);
    // }
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

            // Call the service to create the tournament, passing the photo if present
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
    @PutMapping("/{tournamentId}/players/{playerId}")
    public ResponseEntity<Tournament> addPlayerToTournament(
            @PathVariable UUID tournamentId, @PathVariable UUID playerId) {
        Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, playerId);
        return ResponseEntity.ok(updatedTournament);
    }
    // @PutMapping("/{tournamentId}/players/{playerId}")
    // public ResponseEntity<Tournament> addPlayerToTournament(
    //         @PathVariable UUID tournamentId, @PathVariable String playerId) {
    //     try {
    //         UUID playerUUID = UUID.fromString(playerId);  // Convert manually
    //         System.out.println("Player UUID: " + playerUUID);
    //         Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, playerUUID);
    //         return ResponseEntity.ok(updatedTournament);
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.badRequest().body(null);  // Invalid UUID format
    //     }
    // }

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
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the tournament.");
        }
    }

    // Start tournament
    // @PutMapping("/{tournamentId}/start")
    // public ResponseEntity<String> startTournament(@PathVariable UUID tournamentId) {
    //     try {
    //         // Call the service method to start the tournament
    //         tournamentService.startTournament(tournamentId);
    //         return ResponseEntity.ok("Tournament started successfully.");
    //     } catch (IllegalArgumentException | EntityNotFoundException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while starting the tournament.");
    //     }
    // }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while progressing to the next stage.");
        }
    }

}
