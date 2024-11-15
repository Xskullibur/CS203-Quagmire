package com.project.G1_T3.round.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.round.service.RoundServiceImpl;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.common.model.Status;

import com.project.G1_T3.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

@ActiveProfiles("test")
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

    @Mock
    private PlayerProfileService playerProfileService;

    @InjectMocks
    private RoundServiceImpl roundService;

    private Round round = new Round();
    private Stage stage = new Stage();
    List<Match> matches = new ArrayList<>();
    private List<PlayerProfile> sortedPlayers;
    private PlayerProfile player1 = testCreatePlayerProfile(1200f);
    private PlayerProfile player2 = testCreatePlayerProfile(1800f);
    private UUID roundId;
    private UUID stageId;

    @BeforeEach
    void setUp() {
        roundId = UUID.randomUUID();
        stageId = UUID.randomUUID();

        // Initialize a mock stage
        stage.setStageId(stageId);
        stage.setStatus(Status.IN_PROGRESS);

        // Initialize a list of sorted players for createFirstRound
        sortedPlayers = new ArrayList<>();
        sortedPlayers.add(player1);
        sortedPlayers.add(player2);

        Match match1 = new Match();
        match1.setMatchId(UUID.randomUUID());
        match1.setWinnerId(sortedPlayers.get(0).getProfileId());
        match1.setStatus(Status.COMPLETED);

        Match match2 = new Match();
        match2.setMatchId(UUID.randomUUID());
        match2.setWinnerId(sortedPlayers.get(1).getProfileId());
        match2.setStatus(Status.COMPLETED);

        // Initialize a list of matches
        matches = Arrays.asList(match1, match2);

        // Initialize a round for endRound
        round.setRoundId(roundId);
        round.setRoundNumber(1);
        round.setMatches(matches);
        round.setStage(stage);
    }

    PlayerProfile testCreatePlayerProfile(float rating) {

        PlayerProfile playerProfile = new PlayerProfile();
        playerProfile.setProfileId(UUID.randomUUID());
        playerProfile.setUser(new User());
        ;
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
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        // Act
        roundService.createFirstRound(stageId, sortedPlayers);

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
            roundService.createFirstRound(stageId, null);
        });
        assertEquals("Player list must not be null or empty", exception.getMessage());
    }

    @Test
    void createFirstRound_emptyPlayers_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(stageId, new ArrayList<>());
        });
        assertEquals("Player list must not be null or empty", exception.getMessage());
    }

    @Test
    void createFirstRound_lessThanTwoPlayers_throwsException() {
        // Arrange
        List<PlayerProfile> onePlayer = Collections.singletonList(testCreatePlayerProfile(1500f));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roundService.createFirstRound(stageId, onePlayer);
        });
        assertEquals("At least two players are required to create a round", exception.getMessage());
    }

    @Test
    void createFirstRound_stageNotFound_throwsException() {
        // Arrange
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roundService.createFirstRound(stageId, sortedPlayers);
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
        when(roundRepository.findById(roundId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roundService.endRound(roundId);
        });
        assertEquals("Round not found", exception.getMessage());
    }

    @Test
    void endRound_noMatchesFound_throwsException() {
        // Arrange
        round.setMatches(new ArrayList<>());
        when(roundRepository.findById(roundId)).thenReturn(Optional.of(round));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            roundService.endRound(roundId);
        });
        assertEquals("No matches found for this round", exception.getMessage());
    }

    @Test
    void endRound_noWinnerFound_throwsException() {
        // Arrange
        Match match = matches.get(0);
        match.setStatus(Status.COMPLETED); // Set the match status to completed
        round.setMatches(Collections.singletonList(match));

        when(roundRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(playerProfileService.findByProfileId(any(UUID.class))).thenReturn(null); // No winner found

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            roundService.endRound(roundId);
        });
        assertEquals("Winner not found for match with ID: " + match.getMatchId(), exception.getMessage());
    }

    @Test
    void endRound_invalidMatchStatus_throwsException() {
        // Arrange
        Match match = matches.get(0);
        match.setStatus(Status.SCHEDULED); // Set the match status to something other than COMPLETED
        round.setMatches(Collections.singletonList(match));

        when(roundRepository.findById(roundId)).thenReturn(Optional.of(round));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            roundService.endRound(roundId);
        });
        assertEquals("Match " + match.getMatchId() + " has not been completed.", exception.getMessage());
    }

    @Test
    void endRound_multiplePlayersAdvance_createNextRound() {
        // Arrange
        UUID matchId1 = UUID.randomUUID();
        UUID matchId2 = UUID.randomUUID();

        Match match1 = new Match();
        match1.setMatchId(matchId1);
        match1.setWinnerId(player1.getProfileId());
        match1.setStatus(Status.COMPLETED);
        match1.setPlayer1Id(player1.getProfileId());
        match1.setPlayer2Id(UUID.randomUUID());

        Match match2 = new Match();
        match2.setMatchId(matchId2);
        match2.setWinnerId(player2.getProfileId());
        match2.setStatus(Status.COMPLETED);
        match2.setPlayer1Id(player2.getProfileId());
        match2.setPlayer2Id(UUID.randomUUID());

        List<Match> matches = Arrays.asList(match1, match2);
        round.setMatches(matches);
        round.setStatus(Status.IN_PROGRESS); // Add this line

        when(roundRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(playerProfileService.findByProfileId(player1.getProfileId())).thenReturn(player1);
        when(playerProfileService.findByProfileId(player2.getProfileId())).thenReturn(player2);

        // Mock the save method to return the same round
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock for match creation in the next round
        when(matchService.createMatch(any(MatchDTO.class))).thenAnswer(invocation -> {
            Match newMatch = new Match();
            newMatch.setMatchId(UUID.randomUUID());
            newMatch.setStatus(Status.SCHEDULED);
            return newMatch;
        });

        // Act
        roundService.endRound(roundId);

        // Assert
        verify(roundRepository).findById(roundId);
        verify(playerProfileService).findByProfileId(player1.getProfileId());
        verify(playerProfileService).findByProfileId(player2.getProfileId());

        // Capture the arguments passed to roundRepository.save()
        ArgumentCaptor<Round> roundCaptor = ArgumentCaptor.forClass(Round.class);
        verify(roundRepository, times(2)).save(roundCaptor.capture());

        List<Round> savedRounds = roundCaptor.getAllValues();
        assertEquals(2, savedRounds.size());

        // Verify the first saved round (current round)
        Round savedCurrentRound = savedRounds.get(0);
        assertEquals(Status.COMPLETED, savedCurrentRound.getStatus());

        // Verify the second saved round (next round)
        Round savedNextRound = savedRounds.get(1);
        assertEquals(round.getRoundNumber() + 1, savedNextRound.getRoundNumber());
        assertEquals(Status.IN_PROGRESS, savedNextRound.getStatus());
        assertNotNull(savedNextRound.getMatches());
        assertFalse(savedNextRound.getMatches().isEmpty());
    }

    @Test
    void endRound_onePlayerAdvances_endsStage() {
        // Arrange
        Match match = new Match();
        match.setMatchId(UUID.randomUUID()); // Set a matchId
        match.setWinnerId(player1.getProfileId());
        match.setStatus(Status.COMPLETED); // Set the status to COMPLETED
        round.setMatches(Collections.singletonList(match));

        stage.setProgressingPlayers(new HashSet<>());
        stage.getProgressingPlayers().add(player1);

        when(roundRepository.findById(roundId)).thenReturn(Optional.of(round));
        when(playerProfileService.findByProfileId(player1.getProfileId())).thenReturn(player1);

        // Act
        roundService.endRound(roundId);

        // Assert
        verify(stageRepository, times(1)).save(stage);
        assertEquals(player1.getProfileId(), stage.getWinnerId());
        assertEquals(Status.COMPLETED, stage.getStatus());
    }
}
