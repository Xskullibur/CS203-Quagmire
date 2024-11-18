package com.project.G1_T3.stage.service;

import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.round.service.RoundService;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.common.model.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private RoundService roundService;

    @InjectMocks
    private StageServiceImpl stageService;

    private Stage stage;
    private UUID stageId;
    private UUID tournamentId;
    private Tournament tournament;
    private StageDTO stageDTO;
    private PlayerProfile player;

    @BeforeEach
    void setUp() {

        // Initialize UUIDs
        stageId = UUID.randomUUID();
        tournamentId = UUID.randomUUID();

        // Initialize a Tournament object
        tournament = new Tournament();
        tournament.setId(tournamentId);

        // Initialize Stage
        stage = new Stage();
        stage.setStageId(stageId);
        stage.setStageName("Sample Stage");
        stage.setStartDate(LocalDateTime.now());
        stage.setEndDate(LocalDateTime.now().plusDays(1));
        stage.setFormat(Format.SINGLE_ELIMINATION);
        stage.setStatus(Status.SCHEDULED);
        stage.setTournament(tournament);

        // Initialize StageDTO with valid data
        stageDTO = new StageDTO();
        stageDTO.setStageName("Sample Stage");
        stageDTO.setStartDate(LocalDateTime.now());
        stageDTO.setEndDate(LocalDateTime.now().plusDays(1));

        // Mock PlayerProfile objects using Mockito
        PlayerProfile player1 = mock(PlayerProfile.class);
        PlayerProfile player2 = mock(PlayerProfile.class);

        // Set the players with two PlayerProfile instances
        stageDTO.setPlayers(new HashSet<>(Set.of(player1, player2)));

        // Player
        player = new PlayerProfile();
        player.setCurrentRating(1500f);
    }

    @Test
    void testSaveStage() {
        // Arrange
        when(stageRepository.save(stage)).thenReturn(stage);

        // Act
        Stage result = stageService.saveStage(stage);

        // Assert
        verify(stageRepository, times(1)).save(stage);
        assertEquals(stage, result);
    }

    @Test
    void testGetStageById_Success() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        // Act
        Stage result = stageService.getStageById(stageId);

        // Assert
        assertEquals(stage, result);
        verify(stageRepository, times(1)).findById(stageId);
    }

    @Test
    void testGetStageById_NotFound() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.getStageById(stageId);
        });

        assertEquals("Stage not found with id: " + stageId, exception.getMessage());
        verify(stageRepository, times(1)).findById(stageId);
    }

    @Test
    void testFindAllStagesByTournamentIdSortedByCreatedAtAsc() {
        List<Stage> stages = Arrays.asList(stage);

        // Arrange
        when(stageRepository.findByTournamentIdOrderByCreatedAtAsc(tournamentId)).thenReturn(stages);

        // Act
        List<Stage> result = stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId);

        // Assert
        verify(stageRepository, times(1)).findByTournamentIdOrderByCreatedAtAsc(tournamentId);
        assertEquals(stages, result);
    }

    @Test
    void testFindStageByIdAndTournamentId_Success() {
        // Arrange
        when(stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)).thenReturn(Optional.of(stage));

        // Act
        Stage result = stageService.findStageByIdAndTournamentId(stageId, tournamentId);

        // Assert
        verify(stageRepository, times(1)).findByStageIdAndTournamentId(stageId, tournamentId);
        assertEquals(stage, result);
    }

    @Test
    void testFindStageByIdAndTournamentId_StageNotFound() {
        // Arrange
        when(stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stageService.findStageByIdAndTournamentId(stageId, tournamentId);
        });

        assertEquals("Stage not found", exception.getMessage());
        verify(stageRepository, times(1)).findByStageIdAndTournamentId(stageId, tournamentId);
    }

    @Test
    void testUpdateStageForTournament_Success() {
        // Arrange
        Stage updatedStage = new Stage();
        updatedStage.setStageName("Updated Stage");
        updatedStage.setStartDate(LocalDateTime.now().plusDays(2));
        updatedStage.setEndDate(LocalDateTime.now().plusDays(3));
        updatedStage.setStatus(Status.IN_PROGRESS);
        updatedStage.setFormat(Format.DOUBLE_ELIMINATION);

        when(stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)).thenReturn(Optional.of(stage));
        when(stageRepository.save(any(Stage.class))).thenReturn(updatedStage);

        // Act
        Stage result = stageService.updateStageForTournament(tournamentId, stageId, updatedStage);

        // Assert
        assertEquals("Updated Stage", result.getStageName());
        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    void testUpdateStageForTournament_StageNotFound() {
        // Arrange
        when(stageRepository.findByStageIdAndTournamentId(stageId, tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.updateStageForTournament(tournamentId, stageId, stage);
        });

        assertEquals("Stage not found with id: " + stageId, exception.getMessage());
    }

    @Test
    void testDeleteStageByTournamentId_Success() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        // Act
        stageService.deleteStageByTournamentId(tournamentId, stageId);

        // Assert
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void testDeleteStageByTournamentId_StageNotFound() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.deleteStageByTournamentId(tournamentId, stageId);
        });

        assertEquals("Stage not found with id: " + stageId, exception.getMessage());
    }

    @Test
    void testStartStage_Success() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        // Ensure the stage has players to avoid the "no players" exception
        PlayerProfile player1 = mock(PlayerProfile.class);
        PlayerProfile player2 = mock(PlayerProfile.class);
        Set<PlayerProfile> players = new HashSet<>(Set.of(player1, player2));
        stage.setPlayers(players);

        // Act
        stageService.startStage(stageId);

        // Assert
        verify(roundService, times(1)).createFirstRound(eq(stageId), anyList());
        verify(stageRepository, times(1)).save(stage);
        assertEquals(Status.IN_PROGRESS, stage.getStatus());
    }

    @Test
    void testStartStage_ErrorInSavingStage_ThrowsException() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        PlayerProfile player1 = mock(PlayerProfile.class);
        PlayerProfile player2 = mock(PlayerProfile.class);
        Set<PlayerProfile> players = new HashSet<>(Set.of(player1, player2));
        stage.setPlayers(players);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.createStage(stageDTO, tournament);
        });
        assertEquals("Stage name is required", exception.getMessage());
    }

    @Test
    void testCreateStage_StartDateIsNull_ThrowsException() {
        // Arrange
        stageDTO.setStartDate(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.createStage(stageDTO, tournament);
        });
        assertEquals("Start date is required", exception.getMessage());
    }

    @Test
    void testCreateStage_EndDateIsNull_ThrowsException() {
        // Arrange
        stageDTO.setEndDate(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.createStage(stageDTO, tournament);
        });
        assertEquals("End date is required", exception.getMessage());
    }

    @Test
    void testCreateStage_EndDateBeforeStartDate_ThrowsException() {
        // Arrange
        stageDTO.setStartDate(LocalDateTime.now().plusDays(1));
        stageDTO.setEndDate(LocalDateTime.now());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.createStage(stageDTO, tournament);
        });
        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    @Test
    void testCreateStage_LessThanTwoPlayers_ThrowsException() {
        // Arrange
        stageDTO.setPlayers(new HashSet<>()); // No players

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stageService.createStage(stageDTO, tournament);
        });
        assertEquals("There must be more than 1 player", exception.getMessage());
    }

    @Test
    void testCreateStage_SaveSuccess() {
        // Arrange
        when(stageRepository.save(any(Stage.class))).thenReturn(new Stage());

        // Act
        stageService.createStage(stageDTO, tournament);

        // Assert
        verify(stageRepository, times(1)).save(any(Stage.class));
    }

    @Test
    void testCreateStage_SaveFailure_ThrowsException() {
        // Arrange
        when(stageRepository.save(any(Stage.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stageService.startStage(stageId);
        });

        assertEquals("Error saving the stage: Database error", exception.getMessage());
    }

    // Additional test cases for validations in createStage have been retained
}
