package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface TournamentService {

    List<Tournament> findAllTournaments();    // Get all tournaments

    Tournament findTournamentById(Long id);   // Find a specific tournament by ID

    Page<Tournament> findUpcomingTournaments(Pageable pageable);

    Page<Tournament> findPastTournaments(Pageable pageable);

    List<Tournament> findTournamentsByLocation(String location);  // Filter tournaments by location

    Page<Tournament> getAllTournaments(Pageable pageable);

    List<Tournament> searchByName(String name);

    Tournament createTournament(Tournament tournament);

    Set<PlayerProfile> getPlayersInTournament(Long tournamentId);

    Tournament addPlayerToTournament(Long tournamentId, UUID user_id);
}
