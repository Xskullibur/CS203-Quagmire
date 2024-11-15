package com.project.G1_T3.tournament.service;

import com.project.G1_T3.common.exception.tournament.NoStagesDefinedException;
import com.project.G1_T3.common.exception.tournament.InsufficientPlayersException;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Optional;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private StageService stageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUpcomingTournaments() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.findByStartDateAfter(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findUpcomingTournaments(pageable);
        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }
    
    @Test
    void testFindPastTournaments() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.findByStatus(eq(Status.COMPLETED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findPastTournaments(pageable);
        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testFindCurrentTournaments() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.findByStatus(eq(Status.IN_PROGRESS), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findCurrentTournaments(pageable);
        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testDeleteTournament_NotFound() {
        UUID tournamentId = UUID.randomUUID();
        when(tournamentRepository.existsById(tournamentId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tournamentService.deleteTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament not found with id: " + tournamentId);
    }

    @Test
    void testDeleteTournament_Success() {
        UUID tournamentId = UUID.randomUUID();
        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);

        tournamentService.deleteTournament(tournamentId);

        verify(tournamentRepository, times(1)).deleteById(tournamentId);
    }

    @Test
    void testFindTournamentById_Success() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        Tournament foundTournament = tournamentService.findTournamentById(tournamentId);

        assertThat(foundTournament).isNotNull();
        assertThat(foundTournament.getId()).isEqualTo(tournamentId);
    }

    @Test
    void testFindTournamentById_NotFound() {
        UUID tournamentId = UUID.randomUUID();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tournamentService.findTournamentById(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament not found with id: " + tournamentId);
    }

    @Test
    void testStartTournament_Success() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>(Arrays.asList(new PlayerProfile(), new PlayerProfile())));
        Stage stage = new Stage();
        stage.setStageId(UUID.randomUUID());
        tournament.setStages(new ArrayList<>(Collections.singletonList(stage)));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId))
                .thenReturn(Collections.singletonList(stage));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament startedTournament = tournamentService.startTournament(tournamentId);

        // Assert
        assertThat(startedTournament).isNotNull();
        assertThat(startedTournament.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(stageService, times(1)).startStage(stage.getStageId());
    }

    @Test
    void testStartTournament_NoStagesDefined() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>(Arrays.asList(new PlayerProfile(), new PlayerProfile())));
        tournament.setStages(Collections.emptyList());

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        NoStagesDefinedException exception = assertThrows(NoStagesDefinedException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament must have at least 1 stage.");
    }

    @Test
    void testStartTournament_InsufficientPlayers() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>(Collections.singletonList(new PlayerProfile())));
        Stage stage = new Stage();
        stage.setStageId(UUID.randomUUID());
        tournament.setStages(new ArrayList<>(Collections.singletonList(stage)));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        InsufficientPlayersException exception = assertThrows(InsufficientPlayersException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament must have more than 1 player to start.");
    }

    @Test
    void testFindTournamentsByAvailability() {
        PageRequest pageable = PageRequest.of(0, 10);
        LocalDate availableStartDate = LocalDate.now();
        LocalDate availableEndDate = LocalDate.now().plusDays(7);
        when(tournamentRepository.findByStartAndEndDateWithinAvailability(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findTournamentsByAvailability(
                pageable, availableStartDate, availableEndDate);

        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testFindTournamentsByStatus() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.findByStatusInOrderByStartDateAsc(
                anyList(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.getTournamentsByStatus("IN_PROGRESS", pageable);

        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testSearchByName() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.searchByName(eq("Test"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.searchByName("Test", pageable);

        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testCreateTournament() {
        // Arrange: Set up a sample TournamentDTO to pass to the service method
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
        tournamentDTO.setDescription("A test tournament");

        // Create a mock MultipartFile for the photo parameter
        MultipartFile mockPhoto = new MockMultipartFile("photo", "tournament.jpg", "image/jpeg", new byte[0]);

        // Create a Tournament entity that the repository will return when saving
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setName(tournamentDTO.getName());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());
        tournament.setDeadline(tournamentDTO.getDeadline());
        tournament.setMaxParticipants(tournamentDTO.getMaxParticipants());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setStatus(Status.SCHEDULED);
        tournament.setNumStages(1);

        // Mock the save operation to return the created Tournament
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act: Call the service method with the TournamentDTO and MultipartFile
        Tournament createdTournament = tournamentService.createTournament(tournamentDTO, mockPhoto);

        // Assert: Verify the returned tournament matches the expected data
        assertThat(createdTournament).isNotNull();
        assertThat(createdTournament.getId()).isEqualTo(tournament.getId());
        assertThat(createdTournament.getName()).isEqualTo(tournamentDTO.getName());
        assertThat(createdTournament.getLocation()).isEqualTo(tournamentDTO.getLocation());
        assertThat(createdTournament.getMaxParticipants()).isEqualTo(tournamentDTO.getMaxParticipants());
        assertThat(createdTournament.getDescription()).isEqualTo(tournamentDTO.getDescription());
        assertThat(createdTournament.getStatus()).isEqualTo(Status.SCHEDULED);
        assertThat(createdTournament.getNumStages()).isEqualTo(1);
    }
}
