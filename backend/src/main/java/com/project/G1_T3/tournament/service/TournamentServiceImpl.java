package com.project.G1_T3.tournament.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.common.model.Status;

import jakarta.persistence.EntityNotFoundException;

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
    private StageRepository stageRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private StageService stageService;

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

    // @Override
    // public Tournament createTournament(Tournament tournament) {
    //     return tournamentRepository.save(tournament);
    // }

    @Override
    public Tournament createTournament(TournamentDTO tournamentDTO) {

        // Create the Tournament entity from the TournamentDTO
        Tournament tournament = new Tournament();
        tournament.setName(tournamentDTO.getName());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());
        tournament.setDeadline(tournamentDTO.getDeadline());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setStatus(tournamentDTO.getStatus() != null ? tournamentDTO.getStatus() : Status.SCHEDULED);  // Set default status to UPCOMING if null
        
        Set<UUID> refereeIds = tournamentDTO.getRefereeIds();
        Set<PlayerProfile> referees = new HashSet<>(playerProfileRepository.findAllById(refereeIds));
        tournament.setReferees(referees);

        // Save the tournament without creating stages yet
        return tournamentRepository.save(tournament);
    }

    public Set<PlayerProfile> getPlayers(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        return tournament.getPlayers();
    }

    public Tournament addPlayerToTournament(Long tournamentId, UUID userId) {

        System.out.println("Adding player: " + userId);

        Tournament tournament = tournamentRepository.findById(tournamentId).get();

        if (tournament == null) {
            System.out.println("Invalid tournament id");
        } else {
            System.out.println("Tournament ID: " + tournament.getId());
        }

        PlayerProfile player = playerProfileRepository.findByProfileId(userId);

        if (player == null) {
            System.out.println("Invalid player id");
        } else {
            System.out.println("Player Name: " + player.getFirstName());
        }

        if (!tournament.getPlayers().contains(player)) {
            tournament.getPlayers().add(player);
            System.out.println("Player added!");
        } else {
            System.out.println("Player is already in tournament.");
        }
        
        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long id, Tournament updatedTournament) {
       updatedTournament.setId(id);
       return tournamentRepository.save(updatedTournament);
    }
    
    // Add this method
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }


    public void startTournament(Long tournamentId, TournamentDTO tournamentDTO) {
        // Retrieve the tournament

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + tournamentId + " not found"));

        // Ensure there are enough players
        if (tournament.getPlayers() == null || tournament.getPlayers().size() <= 1) {
            throw new IllegalArgumentException("Tournament must have more than 1 player to start.");
        }
    
        // Create stages only when the tournament is started
        if (tournamentDTO.getStageDTOs() != null && !tournamentDTO.getStageDTOs().isEmpty()) {
            StageDTO nextStageDTO = tournamentDTO.getStageDTOs().get(tournament.getCurrentStageIndex());
            nextStageDTO.setPlayers(tournament.getPlayers());
            nextStageDTO.setReferees(tournament.getReferees());

            System.out.println("test1");
            stageService.createStage(nextStageDTO, tournament);
            System.out.println("test2");

            tournament.setCurrentStageIndex(tournament.getCurrentStageIndex() + 1);
        }
    
        // Set the tournament as started (IN_PROGRESS)
        tournament.setStatus(Status.IN_PROGRESS);
        tournamentRepository.save(tournament);
    }

    public void progressToNextStage(Long tournamentId, TournamentDTO tournamentDTO) {
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + tournamentId + " not found"));

        // Get the previous stage
        int previousStageIndex = tournament.getCurrentStageIndex() - 1;
        Stage previousStage = stageRepository.findByTournamentId(tournamentId).get(previousStageIndex);
        if (previousStage == null) {
            throw new IllegalStateException("Previous stage not found");
        }

        // Get the progressing players from the previous stage
        Set<PlayerProfile> progressingPlayers = previousStage.getProgressingPlayers();

        // Check if there are more stages to progress to
        if (tournament.getCurrentStageIndex() < tournamentDTO.getStageDTOs().size()) {

            // Get the next stage DTO
            StageDTO nextStageDTO = tournamentDTO.getStageDTOs().get(tournament.getCurrentStageIndex());
            nextStageDTO.setPlayers(progressingPlayers);
            nextStageDTO.setReferees(tournament.getReferees());

            // Create the next stage
            stageService.createStage(nextStageDTO, tournament);

            // Increment the current stage index
            tournament.setCurrentStageIndex(tournament.getCurrentStageIndex() + 1);
            tournamentRepository.save(tournament);

            System.out.println("Progressed to the next stage: " + nextStageDTO.getStageName());
        } else {
            // No more stages left, end the tournament
            UUID winnerId = previousStage.getWinnerId(); // Implement logic to determine the winner
            endTournament(tournament, winnerId);
        }
    }

    private void endTournament(Tournament tournament, UUID winnerId) {
        tournament.setStatus(Status.COMPLETED);
        tournament.setWinnerId(winnerId);
        tournamentRepository.save(tournament);

        System.out.println("Tournament " + tournament.getName() + " has been completed.");
    }
}
