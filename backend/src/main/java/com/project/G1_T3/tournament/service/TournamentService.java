package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

public interface TournamentService {

    List<Tournament> findAllTournaments(); // Get all tournaments

    Tournament findTournamentById(UUID id); // Find a specific tournament by ID
    
    TournamentDTO findTournamentDTO(UUID id);

    Page<Tournament> findUpcomingTournaments(Pageable pageable); // Fetch tournaments starting after now

    Page<Tournament> findPastTournaments(Pageable pageable); // Fetch tournaments ending before now

    Page<Tournament> findTournamentsByDeadline(Pageable pageable, LocalDateTime deadline); // Fetch tournaments with a deadline before a specific date

    List<Tournament> findTournamentsByLocation(String location); // Filter tournaments by location

    Page<Tournament> getAllTournaments(Pageable pageable);

    List<Tournament> searchByName(String name);

    public Tournament createTournament(TournamentDTO tournament);

    Set<PlayerProfile> getPlayers(UUID tournamentId);

    Tournament addPlayerToTournament(UUID tournamentId, UUID user_id);

    Tournament updateTournament(UUID tournamentId, Tournament updatedTournament);
    
    // Add this method
    Tournament getTournamentById(UUID id); // Get tournament by ID

    public void startTournament(UUID tournamentId, TournamentDTO tournamentDTO);

    public void progressToNextStage(UUID tournamentId, TournamentDTO tournamentDTO);
}
