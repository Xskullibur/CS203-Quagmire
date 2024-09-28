package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.matchmaking.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MatchCheckerTest {

    private MatchChecker matchChecker;

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

    @BeforeEach
    void setUp() {
        matchChecker = new MatchChecker(matchmakingService, messagingTemplate, playerProfileRepository, userRepository,
                matchRepository);
    }

    @Test
    void testCheckForMatches_MatchFound() {
        // Create mocks
        MatchmakingService matchmakingService = mock(MatchmakingService.class);
        PlayerProfileRepository playerProfileRepository = mock(PlayerProfileRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        MatchRepository matchRepository = mock(MatchRepository.class);

        // Create MatchChecker instance with mocked dependencies
        MatchChecker matchChecker = new MatchChecker(matchmakingService, messagingTemplate, playerProfileRepository,
                userRepository, matchRepository);

        // Create test data
        Match match = new Match();
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setMeetingLatitude(0.0);
        match.setMeetingLongitude(0.0);

        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        player1.setProfileId(player1Id);

        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());
        player2.setProfileId(player2Id);

        User user1 = new User();
        user1.setUsername("User1");

        User user2 = new User();
        user2.setUsername("User2");

        // Set up mock behavior
        when(matchmakingService.findMatch()).thenReturn(match);
        when(playerProfileRepository.findByProfileId(player1Id)).thenReturn(player1);
        when(playerProfileRepository.findByProfileId(player2Id)).thenReturn(player2);
        when(userRepository.getReferenceById(player1.getUserId())).thenReturn(user1);
        when(userRepository.getReferenceById(player2.getUserId())).thenReturn(user2);

        // Call the method under test
        matchChecker.checkForMatches();

        // Verify the expected behavior
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/solo/match/" + player1.getUserId()),
                (Object) argThat(notification -> notification instanceof MatchNotification &&
                        ((MatchNotification) notification).getOpponentName().equals("User2")));

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/solo/match/" + player2.getUserId()),
                (Object) argThat(notification -> notification instanceof MatchNotification &&
                        ((MatchNotification) notification).getOpponentName().equals("User1")));
    }

    @Test
    void testCheckForMatches_NoMatch() {
        when(matchmakingService.findMatch()).thenReturn(null);

        matchChecker.checkForMatches();

        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}