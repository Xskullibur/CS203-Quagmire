package com.project.G1_T3.tournament.service;

import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;

import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.common.exception.tournament.InsufficientPlayersException;
import com.project.G1_T3.common.exception.tournament.NoStagesDefinedException;
import com.project.G1_T3.common.exception.tournament.StageStartException;
import com.project.G1_T3.common.exception.tournament.TournamentNotFoundException;
import com.project.G1_T3.common.exception.tournament.TournamentUpdateException;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.filestorage.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private PlayerProfileService playerProfileService;

    @Autowired
    private StageService stageService;

    @Autowired
    private FileStorageService fileStorageService;

    Logger logger = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Override
    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAllByOrderByStartDateAsc(pageable);
    }

    // Method to fetch tournaments by their status (IN_PROGRESS or SCHEDULED)
    public Page<Tournament> getTournamentsByStatus(String status, Pageable pageable) {
        // Split the status string into a list of statuses
        List<String> statusList = Arrays.asList(status.split(","));
        
        // Call the repository to fetch the tournaments based on status and pagination
        return tournamentRepository.findByStatusInOrderByStartDateAsc(statusList, pageable);
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
        // Fetch tournaments with status COMPLETE and where endDate is before now
        return tournamentRepository.findByStatus(Status.COMPLETED, pageable);
    }

    @Override
    public Page<Tournament> findCurrentTournaments(Pageable pageable) {
        // Fetch tournaments with status IN_PROGRESS
        return tournamentRepository.findByStatus(Status.IN_PROGRESS, pageable);
    }

    @Override
    public Page<Tournament> findTournamentsByAvailability(Pageable pageable, LocalDate availableStartDate,
            LocalDate availableEndDate) {
        LocalDateTime availableStartDateTime = availableStartDate.atStartOfDay();
        LocalDateTime availableEndDateTime = availableEndDate.atTime(LocalTime.MAX);
        return tournamentRepository.findByStartAndEndDateWithinAvailability(availableStartDateTime,
                availableEndDateTime, pageable);
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
    @Transactional
    public Tournament createTournament(TournamentDTO tournamentDTO, MultipartFile photo) {

        // Validate required fields
        if (tournamentDTO.getName() == null || tournamentDTO.getName().isEmpty()) {
            throw new RuntimeException("Tournament name is required");
        }
        if (tournamentDTO.getLocation() == null || tournamentDTO.getLocation().isEmpty()) {
            throw new RuntimeException("Tournament location is required");
        }
        if (tournamentDTO.getStartDate() == null) {
            throw new RuntimeException("Tournament start date is required");
        }
        if (tournamentDTO.getEndDate() == null) {
            throw new RuntimeException("Tournament end date is required");
        }
        if (tournamentDTO.getDeadline() == null) {
            throw new RuntimeException("Tournament deadline is required");
        }

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

        // Upload the photo if it's provided
        if (photo != null && !photo.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString(); // Generate unique path for each photo
                fileStorageService.uploadFile("tournaments", fileName, photo); // Upload using FileStorageService
                tournament.setPhotoUrl("tournaments/" + fileName); // Store only the filename in the database
            } catch (IOException e) {
                throw new RuntimeException("Error uploading photo", e);
            }
        }

        tournamentRepository.save(tournament);

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
        tournament.setNumStages(tournament.getStages().size());
        // Save the tournament along with its stages
        return tournamentRepository.save(tournament);
    }

    public Set<PlayerProfile> getPlayers(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        return tournament.getPlayers();
    }

    public Tournament addPlayerToTournament(UUID tournamentId, UUID profileId) {

        System.out.println("Adding player: " + profileId);

        Tournament tournament = tournamentRepository.findById(tournamentId).get();

        if (tournament == null) {
            System.out.println("Invalid tournament id");
            return null;
        } else {
            System.out.println("Tournament ID: " + tournament.getId());
        }

        if (tournament.getStatus() != Status.SCHEDULED) {
            System.out.println("Tournament signups are over");
            return null;
        }

        PlayerProfile player = playerProfileService.findByUserId(profileId);

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

    public Tournament deletePlayerFromTournament(UUID tournamentId, UUID userId) {
        System.out.println("Removing player: " + userId);

        // Fetch the tournament by ID
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            System.out.println("Invalid tournament ID");
            return null;
        } else {
            System.out.println("Tournament ID: " + tournament.getId());
        }

        // Fetch the player by ID
        PlayerProfile player = playerProfileRepository.findByUserId(userId);
        if (player == null) {
            System.out.println("Invalid player ID");
            return null;
        } else {
            System.out.println("Player Name: " + player.getFirstName());
        }

        // Check if the player is in the tournament and remove if present
        if (tournament.getPlayers().contains(player)) {
            tournament.getPlayers().remove(player);
            System.out.println("Player removed from tournament.");
        } else {
            System.out.println("Player is not in tournament.");
        }

        // Save and return the updated tournament
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

    public Page<Tournament> findUpcomingTournamentsWithinDateRange(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {
        // Convert LocalDate to LocalDateTime at the start and end of the day
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now();

        return tournamentRepository.findByStartDateBetweenAndStartDateGreaterThanEqual(
                fromDateTime,
                toDateTime,
                now,
                pageable);
    }

    public Page<Tournament> findPastTournamentsWithinDateRange(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {
        // Convert LocalDate to LocalDateTime at the start and end of the day
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now();

        return tournamentRepository.findByStartDateBetweenAndStartDateLessThan(
                fromDateTime,
                toDateTime,
                now,
                pageable);
    }

    public Tournament startTournament(UUID tournamentId) {
        // Retrieve the tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        // Ensure there are enough players
        if (tournament.getPlayers() == null || tournament.getPlayers().size() <= 1) {
            throw new InsufficientPlayersException("Tournament must have more than 1 player to start.");
        }

        if (tournament.getStages() == null || tournament.getStages().isEmpty()) {
            throw new NoStagesDefinedException("Tournament must have at least 1 stage.");
        }

        List<Stage> allStages = stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId);

        Stage curStage = allStages.get(0);
        curStage.setPlayers(new HashSet<>(tournament.getPlayers()));

        int numStages = allStages.size();
        tournament.setNumStages(numStages);

        // Set the tournament as started (IN_PROGRESS)
        tournament.setStatus(Status.IN_PROGRESS);

        try {
            stageService.startStage(curStage.getStageId());
        } catch (Exception e) {
            throw new StageStartException("Failed to start stage: " + e.getMessage());
        }

        try {
            return tournamentRepository.save(tournament);
        } catch (Exception e) {
            throw new TournamentUpdateException("Failed to update tournament: " + e.getMessage());
        }
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
