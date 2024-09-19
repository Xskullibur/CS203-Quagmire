package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.Match;
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
        matchChecker = new MatchChecker(matchmakingService, messagingTemplate, playerProfileRepository, userRepository, matchRepository);
    }

    @Test
    void testCheckForMatches_MatchFound() {
        Match match = new Match();
        match.setPlayer1Id(UUID.randomUUID());
        match.setPlayer2Id(UUID.randomUUID());

        PlayerProfile player1 = new PlayerProfile();
        player1.setUserId(UUID.randomUUID());
        PlayerProfile player2 = new PlayerProfile();
        player2.setUserId(UUID.randomUUID());

        User user1 = new User();
        user1.setUsername("User1");
        User user2 = new User();
        user2.setUsername("User2");

        when(matchmakingService.findMatch()).thenReturn(match);
        when(playerProfileRepository.findByProfileId(match.getPlayer1Id())).thenReturn(player1);
        when(playerProfileRepository.findByProfileId(match.getPlayer2Id())).thenReturn(player2);
        when(userRepository.getReferenceById(player1.getUserId())).thenReturn(user1);
        when(userRepository.getReferenceById(player2.getUserId())).thenReturn(user2);

        matchChecker.checkForMatches();

        verify(messagingTemplate).convertAndSend(eq("/topic/solo/match/" + player1.getUserId()),
                any(MatchNotification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/solo/match/" + player2.getUserId()),
                any(MatchNotification.class));
    }

    @Test
    void testCheckForMatches_NoMatch() {
        when(matchmakingService.findMatch()).thenReturn(null);

        matchChecker.checkForMatches();

        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}