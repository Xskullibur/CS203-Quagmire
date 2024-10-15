package com.project.G1_T3.matchmaking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

class MatchCheckerTests {

    @Mock
    private MatchmakingService matchmakingService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private PlayerProfileRepository playerProfileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchRepository matchRepository;

    private MatchChecker matchChecker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchChecker = new MatchChecker(matchmakingService, messagingTemplate, playerProfileRepository, userRepository,
                matchRepository);
    }

    @Test
    void testCheckForMatches_MatchFound() {
        // Arrange
        Match match = new Match();
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);

        when(matchmakingService.findMatch()).thenReturn(match);
        when(playerProfileRepository.findById(player1Id)).thenReturn(Optional.of(new PlayerProfile()));
        when(playerProfileRepository.findById(player2Id)).thenReturn(Optional.of(new PlayerProfile()));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        // Act
        matchChecker.checkForMatches();

        // Assert
        verify(matchRepository).save(match);
        verify(messagingTemplate, times(2)).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void testCheckForMatches_NoMatchFound() {
        // Arrange
        when(matchmakingService.findMatch()).thenReturn(null);

        // Act
        matchChecker.checkForMatches();

        // Assert
        verify(matchRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void testLogQueueStatus() {
        // Act
        matchChecker.logQueueStatus();

        // Assert
        verify(matchmakingService).printQueueStatus();
    }
}