package com.project.G1_T3.matchmaking.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.MatchNotificationDTO;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * The MatchChecker class is responsible for periodically checking for matches
 * and processing them. It uses the MatchmakingService to find matches and
 * sends notifications to the matched players.
 *
 * <p>
 * This class is annotated with @Slf4j for logging, @Component to indicate
 * that it is a Spring component, and @Scheduled to schedule the match checking
 * and queue logging tasks.
 * </p>
 *
 * <p>
 * Dependencies are injected via the constructor using @Autowired:
 * </p>
 * <ul>
 * <li>MatchmakingService - Service for matchmaking operations</li>
 * <li>SimpMessagingTemplate - Template for sending WebSocket messages</li>
 * <li>PlayerProfileRepository - Repository for player profiles</li>
 * <li>UserRepository - Repository for user data</li>
 * <li>MatchRepository - Repository for match data</li>
 * </ul>
 *
 * <p>
 * Methods:
 * </p>
 * <ul>
 * <li>{@link #checkForMatches()} - Scheduled method that checks for matches
 * every 5 seconds</li>
 * <li>{@link #processMatch(Match)} - Processes a found match, updates match
 * status, and sends notifications</li>
 * <li>{@link #getPlayerProfile(UUID)} - Retrieves a player profile by player
 * ID</li>
 * <li>{@link #updateMatchStatus(Match)} - Updates the status of a match to
 * IN_PROGRESS</li>
 * <li>{@link #sendNotifications(Match, PlayerProfile, PlayerProfile, User, User)}
 * - Sends match notifications to the players</li>
 * <li>{@link #logQueueStatus()} - Scheduled method that logs the queue status
 * every 10 seconds</li>
 * </ul>
 *
 * <p>
 * Exceptions:
 * </p>
 * <ul>
 * <li>RuntimeException - Thrown if a player profile is not found</li>
 * </ul>
 *
 * <p>
 * Logging:
 * </p>
 * <ul>
 * <li>Logs debug information when checking for matches</li>
 * <li>Logs info when a match is found and notifications are sent</li>
 * <li>Logs errors if there is an issue processing a match</li>
 * </ul>
 */
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
            processMatch(match);
        }
    }

    private void processMatch(Match match) {
        try {
            PlayerProfile player1 = getPlayerProfile(match.getPlayer1Id());
            PlayerProfile player2 = getPlayerProfile(match.getPlayer2Id());

            User user1 = player1.getUser();
            User user2 = player2.getUser();

            updateMatchStatus(match);

            sendNotifications(match, player1, player2, user1, user2);

            log.info("Match found and notifications sent for players {} and {}",
                    user1.getUsername(), user2.getUsername());
        } catch (Exception e) {
            log.error("Error processing match: ", e);
        }
    }

    private PlayerProfile getPlayerProfile(UUID playerId) {
        return playerProfileRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    private void updateMatchStatus(Match match) {
        match.setStatus(Status.IN_PROGRESS);
        matchRepository.save(match);
    }

    private void sendNotifications(Match match, PlayerProfile player1, PlayerProfile player2, User user1, User user2) {
        PlayerProfileDTO player1DTO = new PlayerProfileDTO(player1);
        PlayerProfileDTO player2DTO = new PlayerProfileDTO(player2);

        MatchNotificationDTO notificationForPlayer1 = new MatchNotificationDTO(
                match.getMatchId(), match.getMeetingLatitude(), match.getMeetingLongitude(),
                user2.getUsername(), player2DTO);

        MatchNotificationDTO notificationForPlayer2 = new MatchNotificationDTO(
                match.getMatchId(), match.getMeetingLatitude(), match.getMeetingLongitude(),
                user1.getUsername(), player1DTO);

        messagingTemplate.convertAndSend("/topic/solo/match/" + user1.getId(), notificationForPlayer1);
        messagingTemplate.convertAndSend("/topic/solo/match/" + user2.getId(), notificationForPlayer2);
    }

    @Scheduled(fixedRate = 10000)
    public void logQueueStatus() {
        matchmakingService.printQueueStatus();
    }
}
