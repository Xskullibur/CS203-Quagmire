package com.project.G1_T3.round;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.round.service.RoundServiceImpl;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.common.model.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private MatchService matchService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private RoundServiceImpl roundService;

    private Round round = new Round();
    private Stage stage = new Stage();
    List<Match> matches = new ArrayList<>();
    private List<PlayerProfile> sortedPlayers;
    private PlayerProfile referee = testCreatePlayerProfile(2500f);
    private PlayerProfile player1 = testCreatePlayerProfile(1200f);
    private PlayerProfile player2 = testCreatePlayerProfile(1800f);

    // @BeforeEach
    // void setUp() {
    //     // Initialize a mock stage
    //     stage = new Stage();
    //     stage.setStageId(1L);
    //     PlayerProfile referee = testCreatePlayerProfile(2500f); 
    //     Set<PlayerProfile> referees = new HashSet<>();
    //     referees.add(referee);  // Adding the referee to the set
    //     stage.setReferees(referees);

    //     // Initialize a list of sorted players
    //     sortedPlayers = new ArrayList<>();
    //     sortedPlayers.add(testCreatePlayerProfile(1200f));
    //     sortedPlayers.add(testCreatePlayerProfile(1800f));
    // }

    @BeforeEach
    void setUp() {
        // Initialize a mock stage
        stage.setStageId(1L);
        stage.setStatus(Status.IN_PROGRESS);
    
        // Add a referee to the stage
        Set<PlayerProfile> referees = new HashSet<>();
        referees.add(referee);  // Adding the referee to the set
        stage.setReferees(referees);
    
        // Initialize a list of sorted players for createFirstRound
        sortedPlayers = new ArrayList<>();
        sortedPlayers.add(player1);
        sortedPlayers.add(player2);
    
        // Initialize matches for endRound
        Match match1 = new Match();
        match1.setMatchId(1L);
        match1.setWinnerId(sortedPlayers.get(0).getProfileId());  // First player wins
    
        Match match2 = new Match();
        match2.setMatchId(2L);
        match2.setWinnerId(sortedPlayers.get(1).getProfileId());  // Second player wins
    
        // Initialize a list of matches
        matches = Arrays.asList(match1, match2);
    
        // Initialize a round for endRound
        round.setRoundId(1L);
        round.setRoundNumber(1);
        round.setMatches(matches);
        round.setStage(stage);
    }

    PlayerProfile testCreatePlayerProfile(float rating) {

        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileId(UUID.randomUUID());
        playerProfile.setUserId(UUID.randomUUID());
        playerProfile.setFirstName("John");
        playerProfile.setLastName("Doe");
        playerProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        playerProfile.setCountry("USA");
        playerProfile.setBio("An experienced player");
        playerProfile.setCurrentRating(rating);

        assertNotNull(playerProfile);
        assertEquals("John", playerProfile.getFirstName());

        return playerProfile;
    }

    @Test
    void createFirstRound_success() {
        // Arrange
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        // Act
        roundService.createFirstRound(1L, sortedPlayers);

        // Assert
        verify(matchService, times(1)).createMatch(any(MatchDTO.class));
        verify(roundRepository, times(1)).save(any(Round.class));
    }

    @Test
    void createFirstRound_nullStageId_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(null, sortedPlayers);
        });
        assertEquals("Stage ID must not be null", exception.getMessage());
    }

    @Test
    void createFirstRound_nullPlayers_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(1L, null);
        });
        assertEquals("Player list must not be null or empty", exception.getMessage());
    }

    @Test
    void createFirstRound_emptyPlayers_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(1L, new ArrayList<>());
        });
        assertEquals("Player list must not be null or empty", exception.getMessage());
    }

    @Test
    void createFirstRound_lessThanTwoPlayers_throwsException() {
        // Arrange
        List<PlayerProfile> onePlayer = Collections.singletonList(testCreatePlayerProfile(1500f));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(1L, onePlayer);
        });
        assertEquals("At least two players are required to create a round", exception.getMessage());
    }

    @Test
    void createFirstRound_stageNotFound_throwsException() {
        // Arrange
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roundService.createFirstRound(1L, sortedPlayers);
        });
        assertEquals("Stage not found", exception.getMessage());
    }
    


    @Test
    void endRound_nullRoundId_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.endRound(null);
        });
        assertEquals("Round ID must not be null", exception.getMessage());
    }

    @Test
    void endRound_roundNotFound_throwsException() {
        // Arrange
        when(roundRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roundService.endRound(1L);
        });
        assertEquals("Round not found", exception.getMessage());
    }

    @Test
    void endRound_noMatchesFound_throwsException() {
        // Arrange
        round.setMatches(new ArrayList<>());
        when(roundRepository.findById(1L)).thenReturn(Optional.of(round));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            roundService.endRound(1L);
        });
        assertEquals("No matches found for this round", exception.getMessage());
    }

    @Test
    void endRound_noWinnerFound_throwsException() {
        // Arrange
        when(roundRepository.findById(1L)).thenReturn(Optional.of(round));
        when(playerProfileRepository.findByProfileId(any(UUID.class))).thenReturn(null);  // No winner found

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            roundService.endRound(1L);
        });
        assertEquals("Winner not found for match with ID: " + matches.get(0).getMatchId(), exception.getMessage());
    }

    @Test
    void endRound_multiplePlayersAdvance_createNextRound() {
        // Arrange
        when(roundRepository.findById(1L)).thenReturn(Optional.of(round));
        when(playerProfileRepository.findByProfileId(player1.getProfileId())).thenReturn(player1);
        when(playerProfileRepository.findByProfileId(player2.getProfileId())).thenReturn(player2);

        // Act
        roundService.endRound(1L);

        // Assert
        verify(roundRepository, times(1)).findById(1L);
        verify(playerProfileRepository, times(1)).findByProfileId(player1.getProfileId());
        verify(playerProfileRepository, times(1)).findByProfileId(player2.getProfileId());

        // Ensure `createNextRound` is indirectly called by checking if `roundRepository.save` is called
        verify(roundRepository, times(1)).save(any(Round.class));
    }

    @Test
    void endRound_onePlayerAdvances_endsStage() {
        // Arrange
        Match match = new Match();
        match.setWinnerId(player1.getProfileId());  // Only one winner in this round
        round.setMatches(Collections.singletonList(match));

        when(roundRepository.findById(1L)).thenReturn(Optional.of(round));
        when(playerProfileRepository.findByProfileId(player1.getProfileId())).thenReturn(player1);

        // Act
        roundService.endRound(1L);

        // Assert
        verify(stageRepository, times(1)).save(stage);
        assertEquals(player1.getProfileId(), stage.getWinnerId());  // Ensure the winner is set
        assertEquals(Status.COMPLETED, stage.getStatus());
    }
}
