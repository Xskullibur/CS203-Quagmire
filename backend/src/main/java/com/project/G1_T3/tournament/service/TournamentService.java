package com.project.G1_T3.tournament.service;

import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public interface TournamentService {
    Page<Tournament> getAllTournaments(Pageable pageable); // Get all tournaments with pagination

    Tournament findTournamentById(UUID id); // Find a specific tournament by ID

    TournamentDTO findTournamentDTO(UUID id);

    Page<Tournament> searchByName(String name, Pageable pageable); // Search tournaments by name with pagination

    Page<Tournament> findUpcomingTournaments(Pageable pageable); // Fetch tournaments starting after now

    Page<Tournament> findPastTournaments(Pageable pageable); // Fetch tournaments ending before now

    Page<Tournament> findTournamentsByAvailability(Pageable pageable, LocalDate availableStartDate,
            LocalDate availableEndDate); // Search tournaments that start and end within the user's available dates

    Page<Tournament> findRegistrableTournaments(Pageable pageable); // Fetch tournaments with a deadline before now

    Page<Tournament> findTournamentsByLocation(String location, Pageable pageable); // Filter tournaments by location
                                                                                    // with pagination

    Page<Tournament> findByKeywordInDescription(String keyword, Pageable pageable); // Search tournaments by keyword in
                                                                                    // description with pagination

    public Tournament createTournament(TournamentDTO tournament);

    public void startTournament(UUID tournamentId, TournamentDTO tournamentDTO);

    public void progressToNextStage(UUID tournamentId);

    Tournament updateTournament(UUID tournamentId, Tournament updatedTournament); // Update an existing tournament

    void deleteTournament(UUID tournamentId);

    Set<PlayerProfile> getPlayers(UUID tournamentId); // Get players of a specific tournament

    Tournament addPlayerToTournament(UUID tournamentId, UUID userId); // Add a player to a specific tournament

    Tournament deletePlayerFromTournament(UUID tournamentId, UUID userId); // Add a player to a specific tournament

    public Page<Tournament> findUpcomingTournamentsWithinDateRange(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    public Page<Tournament> findPastTournamentsWithinDateRange(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    List<Tournament> findFeaturedTournaments(Pageable pageable); // Find top 5 upcoming tournaments
}
