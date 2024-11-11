package com.project.G1_T3.matchmaking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.matchmaking.model.MatchNotificationDTO;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
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
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setMatchId(UUID.randomUUID());
        match.setMeetingLatitude(1.0);
        match.setMeetingLongitude(1.0);

        // Create and configure User mocks
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(user1.getId()).thenReturn(user1Id);
        when(user2.getId()).thenReturn(user2Id);
        when(user1.getUsername()).thenReturn("user1");
        when(user2.getUsername()).thenReturn("user2");

        // Create and configure PlayerProfile mocks
        PlayerProfile player1Profile = mock(PlayerProfile.class);
        PlayerProfile player2Profile = mock(PlayerProfile.class);
        when(player1Profile.getUser()).thenReturn(user1);
        when(player2Profile.getUser()).thenReturn(user2);

        // Configure repository mocks
        when(matchmakingService.findMatch()).thenReturn(match);
        when(playerProfileRepository.findById(player1Id)).thenReturn(Optional.of(player1Profile));
        when(playerProfileRepository.findById(player2Id)).thenReturn(Optional.of(player2Profile));

        // Act
        matchChecker.checkForMatches();

        // Assert
        verify(matchmakingService).findMatch();
        verify(playerProfileRepository).findById(player1Id);
        verify(playerProfileRepository).findById(player2Id);
        verify(matchRepository).save(match);

        // Verify messaging template calls with the correct topic paths
        verify(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + user1Id),
                any(MatchNotificationDTO.class));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/solo/match/" + user2Id),
                any(MatchNotificationDTO.class));
    }

    @Test
    void testCheckForMatches_NoMatchFound() {
        // Arrange
        when(matchmakingService.findMatch()).thenReturn(null);

        // Act
        matchChecker.checkForMatches();

        // Assert
        verify(matchRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(
                anyString(),
                any(MatchNotificationDTO.class));
    }

    @Test
    void testLogQueueStatus() {
        // Act
        matchChecker.logQueueStatus();

        // Assert
        verify(matchmakingService).printQueueStatus();
    }
}
