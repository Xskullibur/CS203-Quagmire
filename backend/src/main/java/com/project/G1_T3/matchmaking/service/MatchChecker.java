package com.project.G1_T3.matchmaking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MatchChecker {

    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerProfileRepository playerProfileRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public MatchChecker(MatchmakingService matchmakingService, SimpMessagingTemplate messagingTemplate,
            PlayerProfileRepository playerProfileRepository, UserRepository userRepository,
            MatchRepository matchRepository) {
        this.matchmakingService = matchmakingService;
        this.messagingTemplate = messagingTemplate;
        this.playerProfileRepository = playerProfileRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkForMatches() {
        log.debug("Checking for matches...");
        matchmakingService.printQueueStatus();

        Match match = matchmakingService.findMatch();
        if (match != null) {
            try {
                PlayerProfile player1 = playerProfileRepository.findById(match.getPlayer1Id())
                        .orElseThrow(() -> new RuntimeException("Player 1 not found"));
                PlayerProfile player2 = playerProfileRepository.findById(match.getPlayer2Id())
                        .orElseThrow(() -> new RuntimeException("Player 2 not found"));

                User user1 = userRepository.findById(player1.getUserId())
                        .orElseThrow(() -> new RuntimeException("User 1 not found"));
                User user2 = userRepository.findById(player2.getUserId())
                        .orElseThrow(() -> new RuntimeException("User 2 not found"));

                match.setStatus(Status.IN_PROGRESS);
                matchRepository.save(match);

                MatchNotification notificationForPlayer1 = new MatchNotification(match, user2.getUsername(), player2);
                MatchNotification notificationForPlayer2 = new MatchNotification(match, user1.getUsername(), player1);

                messagingTemplate.convertAndSend("/topic/solo/match/" + player1.getUserId(), notificationForPlayer1);
                messagingTemplate.convertAndSend("/topic/solo/match/" + player2.getUserId(), notificationForPlayer2);

                log.info("Match found and notifications sent for players {} and {}", user1.getUsername(),
                        user2.getUsername());
            } catch (Exception e) {
                log.error("Error processing match: ", e);
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    public void logQueueStatus() {
        matchmakingService.printQueueStatus();
    }
}