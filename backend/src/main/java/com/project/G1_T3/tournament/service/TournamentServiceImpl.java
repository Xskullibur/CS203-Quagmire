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

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    @Override
    public Tournament findTournamentById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }

    @Override
    public Page<Tournament> searchByName(String name, Pageable pageable) {
        return tournamentRepository.searchByName(name, pageable);
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
    public Page<Tournament> findTournamentsByAvailability(Pageable pageable, LocalDateTime availableStartDate, LocalDateTime availableEndDate) {
        return tournamentRepository.findByStartAndEndDateWithinAvailability(availableStartDate, availableEndDate, pageable);
    }

    @Override
    public Page<Tournament> findRegistrableTournaments(Pageable pageable) {
        return tournamentRepository.findByDeadlineBefore(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<Tournament> findTournamentsByLocation(String location, Pageable pageable) {
        return tournamentRepository.findByLocation(location, pageable);
    }

    @Override
    public Page<Tournament> findByKeywordInDescription(String keyword, Pageable pageable) {
        return tournamentRepository.findByKeywordInDescription(keyword, pageable);
    }

    @Override
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament updateTournament(UUID id, Tournament updatedTournament) {
        updatedTournament.setId(id);
        return tournamentRepository.save(updatedTournament);
    }

    @Override
    public void deleteTournament(UUID tournamentId) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new NoSuchElementException("Tournament not found with id: " + tournamentId);
        }
        tournamentRepository.deleteById(tournamentId);
    }

    @Override
    public Set<PlayerProfile> getPlayers(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + tournamentId));
        return tournament.getPlayers();
    }

    @Override
    public Tournament addPlayerToTournament(UUID tournamentId, UUID userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + tournamentId));
        PlayerProfile player = playerProfileRepository.findByUserId(userId);
        tournament.getPlayers().add(player);
        return tournamentRepository.save(tournament);
    }
}
