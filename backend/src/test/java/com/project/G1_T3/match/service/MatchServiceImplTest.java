package com.project.G1_T3.match.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.common.model.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerProfileService playerProfileService;

    @InjectMocks
    private MatchServiceImpl matchService;

    private MatchDTO matchDTO;
    private Match match;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup dummy match data
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        UUID refereeId = UUID.randomUUID();

        matchDTO = new MatchDTO();
        matchDTO.setPlayer1Id(player1Id);
        matchDTO.setPlayer2Id(player2Id);
        matchDTO.setRefereeId(refereeId);
        matchDTO.setScheduledTime(LocalDateTime.now().plusMinutes(5));
        matchDTO.setWinnerId(player1Id);  // Player 1 is the winner
        matchDTO.setScore("0");

        match = new Match();
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setRefereeId(refereeId);
        match.setWinnerId(player1Id);
        match.setScheduledTime(LocalDateTime.now().plusMinutes(5));
        match.setStatus(Status.SCHEDULED);
    }

    @Test
    void startMatch_ShouldStartMatchIfValidReferee() {
        UUID matchId = UUID.randomUUID();

        // Mock repository call to return the match
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Call the method to test
        matchService.startMatch(matchId, matchDTO);

        // Verify match is updated and saved
        assertEquals(Status.IN_PROGRESS, match.getStatus(), "Match status should be IN_PROGRESS");
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void startMatch_ShouldThrowExceptionIfInvalidReferee() {
        UUID matchId = UUID.randomUUID();
        matchDTO.setRefereeId(UUID.randomUUID());  // Invalid referee ID

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Expect an exception due to unauthorized referee
        Exception exception = assertThrows(RuntimeException.class, () -> {
            matchService.startMatch(matchId, matchDTO);
        });

        assertEquals("Unauthorized referee", exception.getMessage());
    }

    @Test
    void completeMatch_ShouldCompleteMatchAndUpdatePlayerRatings() {
        UUID matchId = UUID.randomUUID();
        PlayerProfile player1 = new PlayerProfile();
        player1.setGlickoRating(1500);
        player1.setRatingDeviation(300.0f);
        player1.setVolatility(0.06f);

        PlayerProfile player2 = new PlayerProfile();
        player2.setGlickoRating(1400);
        player2.setRatingDeviation(350.0f);
        player2.setVolatility(0.07f);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(playerProfileService.findByProfileId(match.getPlayer1Id().toString())).thenReturn(player1);
        when(playerProfileService.findByProfileId(match.getPlayer2Id().toString())).thenReturn(player2);

        // Call the method to test
        matchService.startMatch(matchId, matchDTO);
        matchService.completeMatch(matchId, matchDTO);

        // Verify match status is completed
        assertEquals(Status.COMPLETED, match.getStatus(), "Match status should be COMPLETED");
        //once for match start, once for match complete
        verify(matchRepository, times(2)).save(match);

        // Verify player ratings are updated and saved
        verify(playerProfileService, times(1)).updatePlayerRating(player1);
        verify(playerProfileService, times(1)).updatePlayerRating(player2);
    }

    @Test
    void completeMatch_ShouldThrowExceptionIfInvalidReferee() {
        UUID matchId = UUID.randomUUID();
        matchDTO.setRefereeId(UUID.randomUUID());  // Invalid referee ID

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Expect an exception due to unauthorized referee
        Exception exception = assertThrows(RuntimeException.class, () -> {
            matchService.completeMatch(matchId, matchDTO);
        });

        assertEquals("Unauthorized referee", exception.getMessage());
    }

    @Test
    void completeMatch_ShouldThrowExceptionIfWinnerIdIsInvalid() {
        UUID matchId = UUID.randomUUID();
        matchDTO.setWinnerId(UUID.randomUUID());  // Invalid winner ID

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Expect an exception due to invalid winner ID
        Exception exception = assertThrows(RuntimeException.class, () -> {
            matchService.completeMatch(matchId, matchDTO);
        });

        assertEquals("Winner must be one of the players", exception.getMessage());
    }
}
