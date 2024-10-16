package com.project.G1_T3.tournament;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.stage.model.Format;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.model.StageDTO;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.tournament.service.TournamentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;
    
    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private StageService stageService;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTournaments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tournament> tournamentList = Arrays.asList(new Tournament(), new Tournament());
        Page<Tournament> expectedPage = new PageImpl<>(tournamentList);

        when(tournamentRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Tournament> result = tournamentService.getAllTournaments(pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);
        verify(tournamentRepository).findAll(pageable);
    }

    @Test
    void testFindTournamentById_TournamentExists() {
        UUID tournamentId = UUID.randomUUID();
        Tournament expectedTournament = new Tournament();
        expectedTournament.setId(tournamentId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(expectedTournament));

        Tournament result = tournamentService.findTournamentById(tournamentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tournamentId);
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void testFindTournamentById_TournamentNotFound() {
        UUID tournamentId = UUID.randomUUID();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.findTournamentById(tournamentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Tournament not found with id: " + tournamentId);
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void testSearchByName() {
        String name = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<Tournament> tournamentList = Arrays.asList(new Tournament(), new Tournament());
        Page<Tournament> expectedPage = new PageImpl<>(tournamentList);

        when(tournamentRepository.searchByName(name, pageable)).thenReturn(expectedPage);

        Page<Tournament> result = tournamentService.searchByName(name, pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);
        verify(tournamentRepository).searchByName(name, pageable);
    }

    @Test
    void testFindUpcomingTournaments() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tournament> tournamentList = Arrays.asList(new Tournament(), new Tournament());
        Page<Tournament> expectedPage = new PageImpl<>(tournamentList);

        when(tournamentRepository.findByStartDateAfter(any(LocalDateTime.class), eq(pageable))).thenReturn(expectedPage);

        Page<Tournament> result = tournamentService.findUpcomingTournaments(pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);
        verify(tournamentRepository).findByStartDateAfter(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void testCreateTournament() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("Test Location");
        tournamentDTO.setStartDate(LocalDateTime.now().plusDays(1));
        tournamentDTO.setEndDate(LocalDateTime.now().plusDays(2));
        tournamentDTO.setDeadline(LocalDateTime.now().plusDays(3));
        tournamentDTO.setDescription("Test Description");
        tournamentDTO.setRefereeIds(Set.of(UUID.randomUUID()));
        
        Tournament expectedTournament = new Tournament();
        expectedTournament.setName(tournamentDTO.getName());
        expectedTournament.setLocation(tournamentDTO.getLocation());

        when(playerProfileRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(expectedTournament);

        Tournament result = tournamentService.createTournament(tournamentDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(tournamentDTO.getName());
        assertThat(result.getLocation()).isEqualTo(tournamentDTO.getLocation());
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void testAddPlayerToTournament_PlayerAdded() {
        UUID tournamentId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPlayers(new HashSet<>());

        PlayerProfile player = new PlayerProfile();
        player.setProfileId(playerId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerProfileRepository.findByProfileId(playerId)).thenReturn(player);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.addPlayerToTournament(tournamentId, playerId);

        assertThat(result).isNotNull();
        assertThat(result.getPlayers()).contains(player);
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void testUpdateTournament() {
        UUID tournamentId = UUID.randomUUID();
        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        Tournament result = tournamentService.updateTournament(tournamentId, updatedTournament);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Tournament");
        assertThat(result.getId()).isEqualTo(tournamentId);
        verify(tournamentRepository).save(updatedTournament);
    }

    @Test
    void testStartTournament() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setPlayers(new HashSet<>(Arrays.asList(new PlayerProfile(), new PlayerProfile())));
        tournament.setReferees(new HashSet<>(Collections.singletonList(new PlayerProfile())));

        TournamentDTO tournamentDTO = new TournamentDTO();
        StageDTO stageDTO = new StageDTO();
        stageDTO.setFormat(Format.SINGLE_ELIMINATION);
        tournamentDTO.setStageDTOs(Collections.singletonList(stageDTO));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        tournamentService.startTournament(tournamentId, tournamentDTO);

        assertThat(tournament.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(tournament.getNumStages()).isEqualTo(1);
        verify(tournamentRepository).save(tournament);
        verify(stageService).createStage(any(StageDTO.class), eq(tournament));
    }

    @Test
    void testProgressToNextStage() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setCurrentStageIndex(0);
        tournament.setNumStages(2);

        Stage currentStage = new Stage();
        currentStage.setProgressingPlayers(new HashSet<>(Collections.singletonList(new PlayerProfile())));

        Stage nextStage = new Stage();

        List<Stage> stages = Arrays.asList(currentStage, nextStage);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(stageService.findAllStagesByTournamentIdSortedByCreatedAtAsc(tournamentId)).thenReturn(stages);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        tournamentService.progressToNextStage(tournamentId);

        assertThat(tournament.getCurrentStageIndex()).isEqualTo(1);
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void testDeleteTournament() {
        UUID tournamentId = UUID.randomUUID();

        when(tournamentRepository.existsById(tournamentId)).thenReturn(true);

        tournamentService.deleteTournament(tournamentId);

        verify(tournamentRepository).deleteById(tournamentId);
    }

    @Test
    void testDeleteTournament_TournamentNotFound() {
        UUID tournamentId = UUID.randomUUID();

        when(tournamentRepository.existsById(tournamentId)).thenReturn(false);

        assertThatThrownBy(() -> tournamentService.deleteTournament(tournamentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Tournament not found with id: " + tournamentId);
    }
}