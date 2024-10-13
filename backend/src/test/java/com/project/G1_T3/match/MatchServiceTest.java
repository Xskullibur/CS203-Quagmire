package com.project.G1_T3.match;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchServiceImpl;
import com.project.G1_T3.match.repository.MatchRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchServiceImpl matchServiceImpl;

    private MatchDTO matchDTO;
    private Match match;
    private Long validMatchId;

    @BeforeEach
    void setUp() {
        // Setup for MatchDTO
        matchDTO = new MatchDTO();
        matchDTO.setPlayer1Id(UUID.randomUUID());
        matchDTO.setPlayer2Id(UUID.randomUUID());
        matchDTO.setRefereeId(UUID.randomUUID());
        matchDTO.setScheduledTime(LocalDateTime.now().plusDays(1));
        matchDTO.setScore("0-0");

        // Setup for Match
        validMatchId = 1L;
        match = new Match();
        match.setMatchId(validMatchId);
        match.setRefereeId(matchDTO.getRefereeId());
        match.setStatus(Status.SCHEDULED);
    }

    void setUpForCompleteMatch() {
        matchDTO.setScore("3-0");
        matchDTO.setWinnerId(matchDTO.getPlayer1Id());

        match.setPlayer1Id(matchDTO.getPlayer1Id());
        match.setPlayer2Id(matchDTO.getPlayer2Id());
        match.setStatus(Status.IN_PROGRESS);
    }

    @Test
    void createMatch_validInput_createsMatch() {
        // Arrange
        Match expectedMatch = new Match();
        expectedMatch.setPlayer1Id(matchDTO.getPlayer1Id());
        expectedMatch.setPlayer2Id(matchDTO.getPlayer2Id());
        expectedMatch.setRefereeId(matchDTO.getRefereeId());
        expectedMatch.setScheduledTime(matchDTO.getScheduledTime());
        expectedMatch.setStatus(Status.SCHEDULED);

        when(matchRepository.save(any(Match.class))).thenReturn(expectedMatch);

        // Act
        Match createdMatch = matchServiceImpl.createMatch(matchDTO);

        // Assert
        assertNotNull(createdMatch);
        assertEquals(Status.SCHEDULED, createdMatch.getStatus());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void createMatch_nullPlayer1Id_throwsException() {
        // Arrange
        matchDTO.setPlayer1Id(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.createMatch(matchDTO);
        });
        assertEquals("Player 1 ID must not be null", exception.getMessage());
    }

    @Test
    void createMatch_nullPlayer2Id_throwsException() {
        // Arrange
        matchDTO.setPlayer2Id(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.createMatch(matchDTO);
        });
        assertEquals("Player 2 ID must not be null", exception.getMessage());
    }

    @Test
    void createMatch_samePlayerIds_throwsException() {
        // Arrange
        UUID samePlayerId = UUID.randomUUID();
        matchDTO.setPlayer1Id(samePlayerId);
        matchDTO.setPlayer2Id(samePlayerId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.createMatch(matchDTO);
        });
        assertEquals("Player 1 and Player 2 cannot be the same", exception.getMessage());
    }

    @Test
    void createMatch_scheduledTimeIsNull_throwsException() {
        // Arrange
        matchDTO.setScheduledTime(null);  // Set the scheduled time to null

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.createMatch(matchDTO);
        });
        assertEquals("Scheduled time must not be null", exception.getMessage());
    }

    @Test
    void createMatch_scheduledTimeInPast_throwsException() {
        // Arrange
        matchDTO.setScheduledTime(LocalDateTime.now().minusDays(1));  // Set the scheduled time to the past

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.createMatch(matchDTO);
        });
        assertEquals("Scheduled time must be in the future", exception.getMessage());
    }



    @Test
    void startMatch_matchIdNull_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.startMatch(null, matchDTO);
        });
        assertEquals("Match ID and match details must not be null", exception.getMessage());
    }

    @Test
    void startMatch_matchDTONull_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.startMatch(validMatchId, null);
        });
        assertEquals("Match ID and match details must not be null", exception.getMessage());
    }

    @Test
    void startMatch_matchNotFound_throwsException() {
        // Arrange
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchServiceImpl.startMatch(validMatchId, matchDTO);
        });
        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    void startMatch_matchNotScheduled_throwsException() {
        // Arrange
        match.startMatch(); // Simulate match already started
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            matchServiceImpl.startMatch(validMatchId, matchDTO);
        });
        assertEquals("Match is not scheduled", exception.getMessage());
    }

    @Test
    void startMatch_unauthorizedReferee_throwsException() {
        // Arrange
        matchDTO.setRefereeId(UUID.randomUUID()); // Set different referee ID
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchServiceImpl.startMatch(validMatchId, matchDTO);
        });
        assertEquals("Unauthorized referee", exception.getMessage());
    }

    @Test
    void startMatch_successfulStart_savesMatch() {
        // Arrange
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act
        matchServiceImpl.startMatch(validMatchId, matchDTO);

        // Assert
        verify(matchRepository, times(1)).save(match);
        assertTrue(match.getStatus() == Status.IN_PROGRESS);  // Assuming there's an `isStarted()` method in the Match class.
    }



    @Test
    void completeMatch_matchIdNull_throwsException() {
        setUpForCompleteMatch();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.completeMatch(null, matchDTO);
        });
        assertEquals("Match ID and match details must not be null", exception.getMessage());
    }

    @Test
    void completeMatch_matchDTONull_throwsException() {
        setUpForCompleteMatch();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, null);
        });
        assertEquals("Match ID and match details must not be null", exception.getMessage());
    }

    @Test
    void completeMatch_matchNotFound_throwsException() {
        setUpForCompleteMatch();
        
        // Arrange
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, matchDTO);
        });
        assertEquals("Match not found", exception.getMessage());
    }

    @Test
    void completeMatch_matchAlreadyCompleted_throwsException() {
        setUpForCompleteMatch();

        // Arrange
        match.completeMatch(match.getPlayer1Id(), "3-0"); // Simulate match completion
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, matchDTO);
        });
        assertEquals("Match is already completed", exception.getMessage());
    }

    @Test
    void completeMatch_unauthorizedReferee_throwsException() {
        setUpForCompleteMatch();

        // Arrange
        matchDTO.setRefereeId(UUID.randomUUID()); // Set an invalid referee ID
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, matchDTO);
        });
        assertEquals("Unauthorized referee", exception.getMessage());
    }

    @Test
    void completeMatch_winnerIdNull_throwsException() {
        // Arrange: Setup valid match and null winner in matchDTO
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));
        
        matchDTO.setWinnerId(null);  // Set Winner ID to null
    
        // Act & Assert: Check that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, matchDTO);
        });
    
        // Assert that the correct exception message is thrown
        assertEquals("Winner ID must not be null", exception.getMessage());
    }

    @Test
    void completeMatch_invalidWinner_throwsException() {
        setUpForCompleteMatch();

        // Arrange
        System.out.println(matchDTO.getWinnerId());
        matchDTO.setWinnerId(UUID.randomUUID()); // Set invalid winner (neither player 1 nor player 2)
        System.out.println(matchDTO.getWinnerId());
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchServiceImpl.completeMatch(validMatchId, matchDTO);
        });
        assertEquals("Winner must be one of the players", exception.getMessage());
    }

    @Test
    void completeMatch_successfulCompletion_savesMatch() {
        setUpForCompleteMatch();

        // Arrange
        when(matchRepository.findById(validMatchId)).thenReturn(Optional.of(match));

        // Act
        matchServiceImpl.completeMatch(validMatchId, matchDTO);

        // Assert
        verify(matchRepository, times(1)).save(match);
        assertTrue(match.getStatus() == Status.COMPLETED);  // Assuming `isCompleted()` method is available in Match
    }
}
