package com.project.G1_T3.tournament.service;

import com.project.G1_T3.tournament.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentService {

    List<Tournament> findAllTournaments();    // Get all tournaments

    Tournament findTournamentById(Long id);   // Find a specific tournament by ID

    Page<Tournament> findUpcomingTournaments(Pageable pageable);  // Find upcoming tournaments, paginated

    List<Tournament> findTournamentsByLocation(String location);  // Filter tournaments by location

    Page<Tournament> getAllTournaments(Pageable pageable);

    List<Tournament> searchByName(String name);

    Tournament createTournament(Tournament tournament);
}
