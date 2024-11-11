package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

public interface TournamentService {
    Page<Tournament> getAllTournaments(Pageable pageable); // Get all tournaments with pagination

    Tournament findTournamentById(UUID id); // Find a specific tournament by ID
    
    TournamentDTO findTournamentDTO(UUID id);
    
    Page<Tournament> searchByName(String name, Pageable pageable); // Search tournaments by name with pagination

    Page<Tournament> findUpcomingTournaments(Pageable pageable); // Fetch tournaments starting after now

    Page<Tournament> findPastTournaments(Pageable pageable); // Fetch tournaments ending before now

    Page<Tournament> findTournamentsByAvailability(Pageable pageable, LocalDateTime availableStartDate, LocalDateTime availableEndDate); // Search tournaments that start and end within the user's available dates

    Page<Tournament> findCurrentTournaments(Pageable pageable);

    // Page<Tournament> findTournamentsByDeadline(Pageable pageable, LocalDateTime deadline); // Fetch tournaments with a deadline before a specific date

    Page<Tournament> findRegistrableTournaments(Pageable pageable); // Fetch tournaments with a deadline before now

    Page<Tournament> findTournamentsByLocation(String location, Pageable pageable); // Filter tournaments by location with pagination

    Page<Tournament> findByKeywordInDescription(String keyword, Pageable pageable); // Search tournaments by keyword in description with pagination

    public Tournament createTournament(TournamentDTO tournament, MultipartFile photo);

    Tournament getTournamentById(UUID id); // Get tournament by ID

    Set<PlayerProfile> getPlayers(UUID tournamentId);

    Tournament addPlayerToTournament(UUID tournamentId, UUID user_id);

    Tournament updateTournament(UUID tournamentId, Tournament updatedTournament);

    public Tournament startTournament(UUID tournamentId);

    public void progressToNextStage(UUID tournamentId);

    void deleteTournament(UUID tournamentId);
    
}
