package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Service implementation for matchmaking functionality.
 */
@Slf4j
@Service
public class MatchmakingServiceImpl implements MatchmakingService {
    private final ConcurrentMap<UUID, QueuedPlayer> playerQueue = new ConcurrentHashMap<>();
    private final MatchmakingAlgorithm matchmakingAlgorithm;
    private final MeetingPointService meetingPointService;
    private final MatchService matchService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerProfileService playerProfileService;

    /**
     * Constructor for MatchmakingServiceImpl.
     *
     * @param matchmakingAlgorithm the matchmaking algorithm to use
     * @param meetingPointService the service to find meeting points
     * @param matchService the service to handle match creation
     * @param messagingTemplate the messaging template for notifications
     * @param playerProfileService the service to handle player profiles
     */
    public MatchmakingServiceImpl(MatchmakingAlgorithm matchmakingAlgorithm,
            MeetingPointService meetingPointService,
            MatchService matchService,
            SimpMessagingTemplate messagingTemplate, PlayerProfileService playerProfileService) {
        this.matchmakingAlgorithm = matchmakingAlgorithm;
        this.meetingPointService = meetingPointService;
        this.matchService = matchService;
        this.messagingTemplate = messagingTemplate;
        this.playerProfileService = playerProfileService;
    }

    /**
     * Adds a player to the matchmaking queue.
     *
     * @param player the player profile
     * @param latitude the latitude of the player's location
     * @param longitude the longitude of the player's location
     */
    @Override
    public void addPlayerToQueue(PlayerProfile player, double latitude, double longitude) {
        log.info("Adding player to queue: {} (ID: {})", player.getUserId(), player.getProfileId());
        QueuedPlayer queuedPlayer = new QueuedPlayer(player, latitude, longitude);
        playerQueue.put(player.getUserId(), queuedPlayer);
        log.info("Player added. Current queue size: {}", playerQueue.size());
    }

    /**
     * Removes a player from the matchmaking queue.
     *
     * @param playerId the UUID of the player to remove
     */
    @Override
    public void removePlayerFromQueue(UUID playerId) {
        log.info("Removing player from queue: {}", playerId);
        if (!playerQueue.containsKey(playerId)) {
            throw new PlayerNotFoundException("Player with ID " + playerId + " not found in queue");
        }
        playerQueue.remove(playerId);
        log.info("Player removed. Current queue size: {}", playerQueue.size());
    }

    /**
     * Attempts to find a match from the players in the queue.
     *
     * @return the match found, or null if no match is found
     */
    @Override
    public Match findMatch() {
        log.info("Attempting to find a match. Current queue size: {}", playerQueue.size());
        if (playerQueue.size() < 2) {
            return null;
        }
        List<QueuedPlayer> players = new ArrayList<>(playerQueue.values());
        players.sort(Comparator.comparingDouble(QueuedPlayer::getPriority).reversed());
        for (int i = 0; i < players.size() - 1; i++) {
            QueuedPlayer player1 = players.get(i);
            QueuedPlayer player2 = players.get(i + 1);
            log.debug("Checking match between {} and {}", player1.getPlayer().getUserId(),
                    player2.getPlayer().getUserId());
            if (matchmakingAlgorithm.isGoodMatch(player1, player2)) {
                playerQueue.remove(player1.getPlayer().getUserId());
                playerQueue.remove(player2.getPlayer().getUserId());
                double[] meetingPoint;
                try {
                    meetingPoint = meetingPointService.findMeetingPoint(player1, player2);
                } catch (MeetingPointNotFoundException e) {
                    log.error("Failed to find meeting point for players {} and {}", player1.getPlayer().getUserId(),
                            player2.getPlayer().getUserId());
                    continue; // Skip this match and try the next pair
                }

                MatchDTO matchDTO = new MatchDTO();
                matchDTO.setPlayer1Id(player1.getPlayer().getProfileId());
                matchDTO.setPlayer2Id(player2.getPlayer().getProfileId());
                matchDTO.setMeetingLatitude(meetingPoint[0]);
                matchDTO.setMeetingLongitude(meetingPoint[1]);

                Match match = matchService.createMatch(matchDTO);
                log.info("Match found: {} vs {}", player1.getPlayer().getUserId(), player2.getPlayer().getUserId());
                notifyPlayersAboutMatch(match);
                return match;
            }
        }
        log.warn("No suitable match found in this iteration");
        throw new MatchmakingException("No suitable match found in this iteration");
    }

    /**
     * Notifies players about the match.
     *
     * @param match the match to notify about
     */
    public void notifyPlayersAboutMatch(Match match) {
        log.info("Notifying players about the match: {}", match.getId());

        // Notify player 1
        String destination1 = "/topic/solo/match/" + match.getPlayer1Id();
        messagingTemplate.convertAndSend(destination1, createMatchNotification(match, match.getPlayer1Id()));
        log.info("Notification sent to player 1: {} at destination: {}", match.getPlayer1Id(), destination1);

        // Notify player 2
        String destination2 = "/topic/solo/match/" + match.getPlayer2Id();
        messagingTemplate.convertAndSend(destination2, createMatchNotification(match, match.getPlayer2Id()));
        log.info("Notification sent to player 2: {} at destination: {}", match.getPlayer2Id(), destination2);
    }

    /**
     * Creates a match notification for a player.
     *
     * @param match the match
     * @param uuid the UUID of the player to notify
     * @return the match notification
     */
    @Override
    public MatchNotification createMatchNotification(Match match, UUID uuid) {
        String opponentId = match.getPlayer1Id().equals(uuid) ? match.getPlayer2Id().toString()
                : match.getPlayer1Id().toString();
        PlayerProfile opponentProfile = playerProfileService.findByProfileId(opponentId);

        return new MatchNotification(
                match,
                opponentProfile.getUsername(),
                opponentProfile);
    }

    /**
     * Triggers the matchmaking process.
     */
    @Override
    public void triggerMatchmaking() {
        log.info("Triggering matchmaking process");
        try {
            findMatch();
        } catch (InsufficientPlayersException e) {
            log.info("Not enough players to start matchmaking: {}", e.getMessage());
        } catch (MatchmakingException e) {
            log.info("Matchmaking process completed without finding a match: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during matchmaking process", e);
        }
    }

    /**
     * Checks if a player is in the queue.
     *
     * @param playerId the UUID of the player
     * @return true if the player is in the queue, false otherwise
     */
    @Override
    public boolean isPlayerInQueue(UUID playerId) {
        return playerQueue.containsKey(playerId);
    }

    /**
     * Prints the current status of the queue.
     */
    @Override
    public void printQueueStatus() {
        log.debug("Current players in queue: {}", playerQueue.size());
        playerQueue.values().forEach(player -> log.debug("Player: {} (ID: {}), Priority: {}, Current Rating: {}",
                player.getPlayer().getUserId(),
                player.getPlayer().getProfileId(),
                player.getPriority(),
                player.getPlayer().getCurrentRating()));
        log.debug("--------------------");
    }
}