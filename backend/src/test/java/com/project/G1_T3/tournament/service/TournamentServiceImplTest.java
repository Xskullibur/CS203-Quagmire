package com.project.G1_T3.tournament.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.common.exception.tournament.InsufficientPlayersException;
import com.project.G1_T3.common.exception.tournament.NoStagesDefinedException;
import com.project.G1_T3.common.exception.tournament.StageStartException;
import com.project.G1_T3.common.exception.tournament.TournamentNotFoundException;
import com.project.G1_T3.common.exception.tournament.TournamentUpdateException;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.filestorage.service.FileStorageService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;

import jakarta.persistence.EntityNotFoundException;

import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;

@ActiveProfiles("test")
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private StageService stageService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private PlayerProfileService playerProfileService;

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
    void testStartTournament_TournamentNotFound() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament with ID " + tournamentId + " not found");
        verify(stageService, never()).startStage(any());
    }
    @Test
    void testStartTournament_StageFailsToStart() {
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
        doThrow(new StageStartException("Failed to start stage")).when(stageService).startStage(stage.getStageId());

        // Act & Assert
        StageStartException exception = assertThrows(StageStartException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to start stage: Failed to start stage");
        verify(stageService, times(1)).startStage(stage.getStageId());
    }

    @Test
    void testStartTournament_TournamentUpdateFails() {
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
        when(tournamentRepository.save(any(Tournament.class)))
                .thenThrow(new TournamentUpdateException("Failed to update tournament"));

        // Act & Assert
        TournamentUpdateException exception = assertThrows(TournamentUpdateException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to update tournament: Failed to update tournament");
        verify(stageService, times(1)).startStage(stage.getStageId());
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

    @Test
    void testCreateTournament_NoPhoto() {
        // Arrange: Set up a sample TournamentDTO to pass to the service method
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
        tournamentDTO.setDescription("A test tournament");
    
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
    
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act: Call the service method with the TournamentDTO and no photo
        Tournament createdTournament = tournamentService.createTournament(tournamentDTO, null);
    
        // Assert
        assertThat(createdTournament).isNotNull();
        assertThat(createdTournament.getPhotoUrl()).isNull(); // Ensure photoUrl is null
    }

    @Test
    void testCreateTournament_PhotoUploadFails() throws IOException {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
        tournamentDTO.setDescription("A test tournament");

        MultipartFile mockPhoto = new MockMultipartFile("photo", "tournament.jpg", "image/jpeg", new byte[10]);

        doThrow(new IOException("File upload failed")).when(fileStorageService).uploadFile(anyString(), anyString(), any(MultipartFile.class));

        // Act & Assert: Ensure RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, mockPhoto);
        });

        assertThat(exception.getMessage()).contains("Error uploading photo");
    }

    @Test
    void testCreateTournament_DefaultStageCreated() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
        tournamentDTO.setDescription("A test tournament");
    
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
    
        // Create a default stage with Format set to SINGLE_ELIMINATION
        Stage defaultStage = new Stage();
        defaultStage.setStageName("Single Elimination");
        defaultStage.setFormat(Format.SINGLE_ELIMINATION);
        tournament.setStages(Collections.singletonList(defaultStage));
    
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        Tournament createdTournament = tournamentService.createTournament(tournamentDTO, null);
    
        // Assert: Verify that the default stage is created
        assertThat(createdTournament).isNotNull();
        assertThat(createdTournament.getStages()).isNotEmpty();
        assertThat(createdTournament.getStages().get(0).getFormat()).isEqualTo(Format.SINGLE_ELIMINATION);
    }

    @Test
    void createTournament_missingName_throwsException() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
    
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament name is required");
    }
    
    @Test
    void createTournament_missingLocation_throwsException() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
    
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament location is required");
    }

    @Test
    void createTournament_missingStartDate_throwsException() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
    
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament start date is required");
    }

    @Test
    void createTournament_missingEndDate_throwsException() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
    
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament end date is required");
    }

    @Test
    void createTournament_missingDeadline_throwsException() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setMaxParticipants(16);
    
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament deadline is required");
    }

    @Test
    void createTournament_allRequiredFieldsProvided_tournamentCreated() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
    
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
    
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        Tournament createdTournament = tournamentService.createTournament(tournamentDTO, null);
    
        // Assert
        assertThat(createdTournament).isNotNull();
        assertThat(createdTournament.getName()).isEqualTo("Test Tournament");
        assertThat(createdTournament.getLocation()).isEqualTo("New York");
        assertThat(createdTournament.getStartDate()).isEqualTo(tournamentDTO.getStartDate());
    }

    @Test
    void testCreateTournament_InvalidTournamentDTO() {
        // Arrange
        TournamentDTO tournamentDTO = new TournamentDTO();
    
        // Act & Assert: Ensure an exception is thrown for missing required fields
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.createTournament(tournamentDTO, null);
        });
    
        assertThat(exception.getMessage()).contains("Tournament name is required");
    }
    
    @Test
    void addPlayerToTournament_validPlayer_playerAdded() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(Status.SCHEDULED);
        tournament.setPlayers(new HashSet<>());
    
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(profileId);
        player.setFirstName("John");
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileService.findByUserId(profileId)).thenReturn(player);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, profileId);
    
        // Assert
        assertThat(updatedTournament).isNotNull();
        assertThat(updatedTournament.getPlayers()).contains(player);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void addPlayerToTournament_invalidTournamentId_throwsException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());
    
        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            tournamentService.addPlayerToTournament(tournamentId, profileId);
        });
    
        assertThat(exception.getMessage()).contains("No value present");
    }

    @Test
    void addPlayerToTournament_signupsClosed_returnsNull() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(Status.IN_PROGRESS); // Signups are over
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
    
        // Act
        Tournament result = tournamentService.addPlayerToTournament(tournamentId, profileId);
    
        // Assert
        assertThat(result).isNull();
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }
    
    @Test
    void addPlayerToTournament_invalidPlayerId_returnsNull() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(Status.SCHEDULED);
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileService.findByUserId(profileId)).thenReturn(null);
    
        // Act
        Tournament result = tournamentService.addPlayerToTournament(tournamentId, profileId);
    
        // Assert
        assertThat(result).isNull();
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }
    
    @Test
    void addPlayerToTournament_playerAlreadyInTournament_noDuplicateAdded() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(profileId); // Ensure the same field used in addPlayerToTournament
        player.setFirstName("John");
        
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(Status.SCHEDULED);
        tournament.setPlayers(new HashSet<>(Collections.singletonList(player)));
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileService.findByUserId(profileId)).thenReturn(player);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
        
        // Act
        Tournament updatedTournament = tournamentService.addPlayerToTournament(tournamentId, profileId);
        
        // Assert
        assertThat(updatedTournament).isNotNull();
        assertThat(updatedTournament.getPlayers()).hasSize(1); // No duplicate added
        assertThat(updatedTournament.getPlayers()).contains(player);
        verify(tournamentRepository, times(1)).save(tournament);
    }
    
    @Test
    void addPlayerToTournament_nullPlayerId_returnsNull() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStatus(Status.SCHEDULED);
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
    
        // Act
        Tournament result = tournamentService.addPlayerToTournament(tournamentId, null);
    
        // Assert
        assertThat(result).isNull();
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void deletePlayerFromTournament_playerExists_playerRemoved() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
    
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(userId);
        player.setFirstName("John");
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>(Collections.singletonList(player)));
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileRepository.findByUserId(userId)).thenReturn(player);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        Tournament updatedTournament = tournamentService.deletePlayerFromTournament(tournamentId, userId);
    
        // Assert
        assertThat(updatedTournament).isNotNull();
        assertThat(updatedTournament.getPlayers()).doesNotContain(player);
        verify(tournamentRepository, times(1)).save(tournament);
    }
    
    @Test
    void deletePlayerFromTournament_invalidTournamentId_returnsNull() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());
    
        // Act
        Tournament result = tournamentService.deletePlayerFromTournament(tournamentId, userId);
    
        // Assert
        assertThat(result).isNull();
        verify(playerProfileRepository, never()).findByUserId(any());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void deletePlayerFromTournament_invalidPlayerId_returnsNull() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>());
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileRepository.findByUserId(userId)).thenReturn(null);
    
        // Act
        Tournament result = tournamentService.deletePlayerFromTournament(tournamentId, userId);
    
        // Assert
        assertThat(result).isNull();
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void deletePlayerFromTournament_playerNotInTournament_noChange() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
    
        PlayerProfile player = new PlayerProfile();
        player.setProfileId(userId);
        player.setFirstName("John");
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>()); // Empty players list
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileRepository.findByUserId(userId)).thenReturn(player);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        Tournament updatedTournament = tournamentService.deletePlayerFromTournament(tournamentId, userId);
    
        // Assert
        assertThat(updatedTournament).isNotNull();
        assertThat(updatedTournament.getPlayers()).doesNotContain(player); // No changes
        verify(tournamentRepository, times(1)).save(tournament); // Save still occurs
    }

    @Test
    void progressToNextStage_success() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setCurrentStageIndex(0);
        tournament.setNumStages(2);
    
        Stage currentStage = new Stage();
        currentStage.setStageId(UUID.randomUUID());
        currentStage.setProgressingPlayers(Set.of(new PlayerProfile()));
    
        Stage nextStage = new Stage();
        nextStage.setStageId(UUID.randomUUID());
        nextStage.setPlayers(new HashSet<>());
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId))
                .thenReturn(List.of(currentStage, nextStage));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);
    
        // Act
        tournamentService.progressToNextStage(tournamentId);
    
        // Assert
        assertThat(tournament.getCurrentStageIndex()).isEqualTo(1); // Current stage index incremented
        assertThat(nextStage.getPlayers()).isEqualTo(currentStage.getProgressingPlayers()); // Players progressed
        verify(tournamentRepository, times(1)).save(tournament);
    }
    
    @Test
    void progressToNextStage_tournamentNotFound_throwsException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tournamentService.progressToNextStage(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Tournament with ID " + tournamentId + " not found");
        verify(stageService, never()).findAllStagesByTournamentIdSortedByCreatedAtAsc(any());
    }

    @Test
    void progressToNextStage_currentStageNotFound_throwsException() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setCurrentStageIndex(0); // Index to fetch the current stage
        tournament.setNumStages(2);

        // Simulate a list of stages with a `null` current stage
        List<Stage> stages = Arrays.asList(null, new Stage()); // First stage is null

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId)).thenReturn(stages);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tournamentService.progressToNextStage(tournamentId);
        });

        assertThat(exception.getMessage()).isEqualTo("Previous stage not found");
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void progressToNextStage_noMoreStages_endTournament() {
        // Arrange
        UUID tournamentId = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID();
    
        PlayerProfile winner = new PlayerProfile();
        winner.setProfileId(winnerId);
    
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setCurrentStageIndex(0);
        tournament.setNumStages(1);
    
        Stage currentStage = new Stage();
        currentStage.setStageId(UUID.randomUUID());
        currentStage.setWinnerId(winnerId);
    
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId)).thenReturn(List.of(currentStage));
    
        // Act
        tournamentService.progressToNextStage(tournamentId);
    
        // Assert
        assertThat(tournament.getCurrentStageIndex()).isEqualTo(0); // No change to current stage index
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void findFeaturedTournaments_returnsClosest5Tournaments() {
        // Arrange
        Tournament t1 = new Tournament();
        t1.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        Tournament t2 = new Tournament();
        t2.setStartDate(LocalDateTime.of(2024, 1, 2, 10, 0));
        Tournament t3 = new Tournament();
        t3.setStartDate(LocalDateTime.of(2024, 1, 3, 10, 0));
        Tournament t4 = new Tournament();
        t4.setStartDate(LocalDateTime.of(2024, 1, 4, 10, 0));
        Tournament t5 = new Tournament();
        t5.setStartDate(LocalDateTime.of(2024, 1, 5, 10, 0));
        Tournament t6 = new Tournament();
        t6.setStartDate(LocalDateTime.of(2024, 1, 6, 10, 0));
    
        // Create a pageable response with 6 tournaments
        List<Tournament> tournaments = List.of(t1, t6, t5, t4, t3, t2); // Unsorted
        Page<Tournament> pageableTournaments = new PageImpl<>(tournaments);
        Pageable pageable = PageRequest.of(0, 10);
    
        when(tournamentRepository.findByStartDateAfter(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(pageableTournaments);
    
        // Act
        List<Tournament> featuredTournaments = tournamentService.findFeaturedTournaments(pageable);
    
        // Assert
        assertThat(featuredTournaments).isNotNull();
        assertThat(featuredTournaments).hasSize(5); // Only the closest 5 tournaments should be returned
        assertThat(featuredTournaments).containsExactly(t1, t2, t3, t4, t5); // Check correct sorting
    }
    
}
