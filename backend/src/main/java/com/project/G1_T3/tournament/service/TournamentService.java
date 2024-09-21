package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

public interface TournamentService {

    List<Tournament> findAllTournaments(); // Get all tournaments

    Tournament findTournamentById(Long id); // Find a specific tournament by ID

    Page<Tournament> findUpcomingTournaments(Pageable pageable); // Fetch tournaments starting after now

    Page<Tournament> findPastTournaments(Pageable pageable); // Fetch tournaments ending before now

    Page<Tournament> findTournamentsByDeadline(Pageable pageable, LocalDateTime deadline); // Fetch tournaments with a deadline before a specific date

    List<Tournament> findTournamentsByLocation(String location); // Filter tournaments by location

    Page<Tournament> getAllTournaments(Pageable pageable);

    List<Tournament> searchByName(String name);

    Tournament createTournament(Tournament tournament);

    Set<PlayerProfile> getPlayersInTournament(Long tournamentId);

    Tournament addPlayerToTournament(Long tournamentId, UUID user_id);

    Tournament updateTournament(Long tournamentId, Tournament updatedTournament);
    
    // Add this method
    Tournament getTournamentById(Long id); // Get tournament by ID
}
