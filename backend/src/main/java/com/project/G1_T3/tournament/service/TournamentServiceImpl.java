package com.project.G1_T3.tournament.service;

import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }

    @Override
    public Page<Tournament> findUpcomingTournaments(Pageable pageable) {
        // Fetch upcoming tournaments (with pagination)
        return tournamentRepository.findByDateAfter(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<Tournament> findPastTournaments(Pageable pageable) {
        // Fetch upcoming tournaments (with pagination)
        return tournamentRepository.findByDateBefore(LocalDateTime.now(), pageable);
    }

    @Override
    public List<Tournament> findTournamentsByLocation(String location) {
        return tournamentRepository.findByLocation(location);
    }
    
    @Override
    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    @Override
    public List<Tournament> searchByName(String name) {
        return tournamentRepository.searchByName(name);
    }

    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
}

