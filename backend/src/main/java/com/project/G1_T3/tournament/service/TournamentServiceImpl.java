package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.repository.TournamentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

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
        return tournamentRepository.findByStartDateAfter(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<Tournament> findPastTournaments(Pageable pageable) {
        return tournamentRepository.findByEndDateBefore(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<Tournament> findTournamentsByDeadline(Pageable pageable, LocalDateTime deadline) {
        return tournamentRepository.findByDeadlineBefore(deadline, pageable);
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

    public Set<PlayerProfile> getPlayersInTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        return tournament.getPlayers();
    }

    public Tournament addPlayerToTournament(Long tournamentId, UUID userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        PlayerProfile player = playerProfileRepository.getPlayerProfileByUserId(userId);
        tournament.getPlayers().add(player);
        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long id, Tournament updatedTournament) {
       updatedTournament.setId(id);
       return tournamentRepository.update(id, updatedTournament);
    }
    
    // Add this method
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }
}
