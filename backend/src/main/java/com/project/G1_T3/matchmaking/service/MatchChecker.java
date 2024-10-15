package com.project.G1_T3.matchmaking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.matchmaking.model.MatchNotificationDTO;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * MatchChecker is responsible for periodically checking for matches and notifying players.
 */
@Slf4j
@Component
public class MatchChecker {

    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerProfileRepository playerProfileRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    /**
     * Constructor for MatchChecker.
     *
     * @param matchmakingService the matchmaking service
     * @param messagingTemplate the messaging template for sending notifications
     * @param playerProfileRepository the repository for player profiles
     * @param userRepository the repository for users
     * @param matchRepository the repository for matches
     */
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

    /**
     * Periodically checks for matches and notifies the players if a match is found.
     * This method is scheduled to run every 5000 milliseconds (5 seconds).
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkForMatches() {
        log.debug("Checking for matches...");
        matchmakingService.printQueueStatus();

        Match match = matchmakingService.findMatch();
        if (match != null) {
            try {
                // Retrieve player profiles and users for the matched players
                PlayerProfile player1 = playerProfileRepository.findById(match.getPlayer1Id())
                        .orElseThrow(() -> new RuntimeException("Player 1 not found"));
                PlayerProfile player2 = playerProfileRepository.findById(match.getPlayer2Id())
                        .orElseThrow(() -> new RuntimeException("Player 2 not found"));

                User user1 = userRepository.findById(player1.getUserId())
                        .orElseThrow(() -> new RuntimeException("User 1 not found"));
                User user2 = userRepository.findById(player2.getUserId())
                        .orElseThrow(() -> new RuntimeException("User 2 not found"));

                // Update match status to IN_PROGRESS and save it
                match.setStatus(Status.IN_PROGRESS);
                matchRepository.save(match);

                // Create DTOs for player profiles
                PlayerProfileDTO player1DTO = new PlayerProfileDTO(player1);
                PlayerProfileDTO player2DTO = new PlayerProfileDTO(player2);

                // Create notifications for both players
                MatchNotificationDTO notificationForPlayer1 = new MatchNotificationDTO(
                        match.getMatchId(),
                        match.getMeetingLatitude(),
                        match.getMeetingLongitude(),
                        user2.getUsername(),
                        player2DTO);

                MatchNotificationDTO notificationForPlayer2 = new MatchNotificationDTO(
                        match.getMatchId(),
                        match.getMeetingLatitude(),
                        match.getMeetingLongitude(),
                        user1.getUsername(),
                        player1DTO);

                // Send notifications to both players
                messagingTemplate.convertAndSend("/topic/solo/match/" + player1.getUserId(), notificationForPlayer1);
                messagingTemplate.convertAndSend("/topic/solo/match/" + player2.getUserId(), notificationForPlayer2);

                log.info("Match found and notifications sent for players {} and {}", user1.getUsername(),
                        user2.getUsername());
            } catch (Exception e) {
                log.error("Error processing match: ", e);
            }
        }
    }

    /**
     * Periodically logs the status of the matchmaking queue.
     * This method is scheduled to run every 10000 milliseconds (10 seconds).
     */
    @Scheduled(fixedRate = 10000)
    public void logQueueStatus() {
        matchmakingService.printQueueStatus();
    }
}