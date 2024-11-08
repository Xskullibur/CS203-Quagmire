package com.project.G1_T3.tournament.service;

import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;

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
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private StageService stageService;

    @Override
    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    @Override
    public Tournament findTournamentById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }

    public TournamentDTO findTournamentDTO(UUID id) {
        Tournament t = findTournamentById(id);
        TournamentDTO result = new TournamentDTO(t);

        return result;
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
    public Page<Tournament> findTournamentsByAvailability(Pageable pageable, LocalDateTime availableStartDate,
            LocalDateTime availableEndDate) {
        return tournamentRepository.findByStartAndEndDateWithinAvailability(availableStartDate, availableEndDate,
                pageable);
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

    // @Override
    // public Tournament createTournament(Tournament tournament) {
    // return tournamentRepository.save(tournament);
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
        tournament.setStatus(tournamentDTO.getStatus() != null ? tournamentDTO.getStatus() : Status.SCHEDULED);
        tournament.setMaxParticipants(tournamentDTO.getMaxParticipants());

        Set<UUID> refereeIds = tournamentDTO.getRefereeIds();
        Set<PlayerProfile> referees = new HashSet<>(playerProfileRepository.findAllById(refereeIds));
        tournament.setReferees(referees);

        // Handle stage creation
        if (tournamentDTO.getStageDTOs() != null && !tournamentDTO.getStageDTOs().isEmpty()) {
            // Convert StageDTO to Stage entity and link with the tournament
            for (StageDTO stageDTO : tournamentDTO.getStageDTOs()) {
                Stage stage = new Stage();
                stage.setStageName(stageDTO.getStageName());
                stage.setStartDate(stageDTO.getStartDate());
                stage.setEndDate(stageDTO.getEndDate());
                stage.setFormat(stageDTO.getFormat());
                stage.setStatus(stageDTO.getStatus() != null ? stageDTO.getStatus() : Status.SCHEDULED);
                stage.setTournament(tournament); // Link the stage with the tournament
                tournament.getStages().add(stage); // Add the stage to the tournament
            }
        } else {
            // Automatically create a default single elimination stage if no stages are
            // provided
            Stage defaultStage = new Stage();
            defaultStage.setStageName("Single Elimination");
            defaultStage.setStartDate(tournamentDTO.getStartDate());
            defaultStage.setEndDate(tournamentDTO.getEndDate());
            defaultStage.setFormat(Format.SINGLE_ELIMINATION); // Assuming this is an enum
            defaultStage.setStatus(Status.SCHEDULED);
            defaultStage.setTournament(tournament); // Link to tournament
            tournament.getStages().add(defaultStage);
        }

        // Save the tournament along with its stages
        return tournamentRepository.save(tournament);
    }

    public Set<PlayerProfile> getPlayers(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        return tournament.getPlayers();
    }

    public Tournament addPlayerToTournament(UUID tournamentId, UUID userId) {

        System.out.println("Adding player: " + userId);

        Tournament tournament = tournamentRepository.findById(tournamentId).get();

        if (tournament == null) {
            System.out.println("Invalid tournament id");
            return null;
        } else {
            System.out.println("Tournament ID: " + tournament.getId());
        }

        PlayerProfile player = playerProfileRepository.findByProfileId(userId);

        if (player == null) {
            System.out.println("Invalid player id");
            return null;
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

    public Tournament updateTournament(UUID id, Tournament updatedTournament) {
        updatedTournament.setId(id);
        return tournamentRepository.save(updatedTournament);
    }

    // Add this method
    public Tournament getTournamentById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found with id: " + id));
    }

    public void startTournament(UUID tournamentId, TournamentDTO tournamentDTO) {
        // Retrieve the tournament

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + tournamentId + " not found"));

        // Ensure there are enough players
        if (tournament.getPlayers() == null || tournament.getPlayers().size() <= 1) {
            throw new IllegalArgumentException("Tournament must have more than 1 player to start.");
        }

        if (tournamentDTO.getStageDTOs() == null || tournamentDTO.getStageDTOs().isEmpty()) {
            throw new IllegalArgumentException("Tournament must have at least 1 stage.");
        }

        int numStages = tournamentDTO.getStageDTOs().size();

        for (int i = 0; i < numStages; i++) {
            StageDTO curStageDTO = tournamentDTO.getStageDTOs().get(i);
            if (i == 0) {
                curStageDTO.setPlayers(tournament.getPlayers());
                curStageDTO.setReferees(tournament.getReferees());
            }

            stageService.createStage(curStageDTO, tournament);
        }

        tournament.setNumStages(numStages);

        // Set the tournament as started (IN_PROGRESS)
        tournament.setStatus(Status.IN_PROGRESS);
        tournamentRepository.save(tournament);
    }

    public void progressToNextStage(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament with ID " + tournamentId + " not found"));

        // Get the previous stage
        int curStageIndex = tournament.getCurrentStageIndex();
        List<Stage> allStages = stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId);
        Stage curStage = allStages.get(curStageIndex);
        if (curStage == null) {
            throw new IllegalStateException("Previous stage not found");
        }

        // Get the progressing players from the previous stage
        Set<PlayerProfile> progressingPlayers = curStage.getProgressingPlayers();

        int nextStageIndex = curStageIndex + 1;

        // Check if there are more stages to progress to
        if (nextStageIndex < tournament.getNumStages()) {

            Stage nextStage = allStages.get(nextStageIndex);

            // Get the next stage DTO
            nextStage.setPlayers(progressingPlayers);
            nextStage.setReferees(tournament.getReferees());

            // Increment the current stage index
            tournament.setCurrentStageIndex(nextStageIndex);
            tournamentRepository.save(tournament);

            System.out.println("Progressed to the next stage: " + nextStage.getStageName());
        } else {
            // No more stages left, end the tournament
            UUID winnerId = curStage.getWinnerId(); // Implement logic to determine the winner
            endTournament(tournament, winnerId);
        }
    }

    private void endTournament(Tournament tournament, UUID winnerId) {
        tournament.setStatus(Status.COMPLETED);
        tournament.setWinnerId(winnerId);
        tournamentRepository.save(tournament);

        System.out.println("Tournament " + tournament.getName() + " has been completed.");
    }

    @Override
    public void deleteTournament(UUID tournamentId) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new NoSuchElementException("Tournament not found with id: " + tournamentId);
        }
        tournamentRepository.deleteById(tournamentId);
    }

    // returns a list of the closest 5 upcoming tournaments
    public List<Tournament> findFeaturedTournaments(Pageable pageable) {
        Page<Tournament> upcomingTournaments = tournamentRepository.findByStartDateAfter(LocalDateTime.now(), pageable);
        
        // Convert to a modifiable list
        List<Tournament> sortedTournaments = new ArrayList<>(upcomingTournaments.getContent());
        
        // Sort the tournaments by start date
        sortedTournaments.sort(Comparator.comparing(Tournament::getStartDate));
        
        // Return the top 5 tournaments
        return sortedTournaments.subList(0, Math.min(5, sortedTournaments.size()));
    }
}
