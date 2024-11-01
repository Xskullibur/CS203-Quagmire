package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
/**
 * Service implementation for matchmaking functionality.
 */
@Slf4j
@Service
public class MatchmakingServiceImpl implements MatchmakingService {
    private final MeetingPointService meetingPointService;
    private final MatchService matchService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerProfileService playerProfileService;

    private final PlayerQueue playerQueue;

    public MatchmakingServiceImpl(
            MeetingPointService meetingPointService,
            MatchService matchService,
            SimpMessagingTemplate messagingTemplate,
            PlayerProfileService playerProfileService,
            PlayerQueue playerQueue) {
        this.meetingPointService = meetingPointService;
        this.matchService = matchService;
        this.messagingTemplate = messagingTemplate;
        this.playerProfileService = playerProfileService;
        this.playerQueue = playerQueue;
    }

    @Override
    public void addPlayerToQueue(PlayerProfile player, double latitude, double longitude) {
        if (playerQueue.containsPlayer(player.getUser().getId())) {
            throw new PlayerAlreadyInQueueException("Player with ID " + player.getUser().getId() + " is already in queue");
        }

        log.info("Adding player to queue: {} (ID: {})", player.getUser().getId(), player.getProfileId());
        playerQueue.addPlayer(player, latitude, longitude);
        log.info("Player added. Current queue size: {}", playerQueue.size());
    }

    @Override
    public void removePlayerFromQueue(UUID playerId) {
        log.info("Removing player from queue: {}", playerId);
        if (!playerQueue.containsPlayer(playerId)) {
            throw new PlayerNotFoundException("Player with ID " + playerId + " not found in queue");
        }
        playerQueue.removePlayer(playerId);
        log.info("Player removed. Current queue size: {}", playerQueue.size());
    }

    /**
     * Attempts to find a match from the players in the queue.
     *
     * @return the match found, or null if no match is found
     */
    @Override
    public Match findMatch() {
        log.info("Attempting to find matches. Current queue size: {}", playerQueue.size());
        if (playerQueue.size() < 2) {
            return null;
        }

        List<QueuedPlayer> allPlayers = playerQueue.getAllPlayers();

        for (QueuedPlayer player : allPlayers) {
            // Attempt to find a match for the player
            QueuedPlayer matchCandidate = playerQueue.findMatch(player);

            if (matchCandidate != null) {
                // Remove both players from the queue
                playerQueue.removePlayer(player.getPlayer().getUser().getId());
                playerQueue.removePlayer(matchCandidate.getPlayer().getUser().getId());

                double[] meetingPoint;
                try {
                    meetingPoint = meetingPointService.findMeetingPoint(player, matchCandidate);
                } catch (MeetingPointNotFoundException e) {
                    log.error("Failed to find meeting point for players {} and {}", player.getPlayer().getUser().getId(),
                            matchCandidate.getPlayer().getUser().getId());
                    continue; // Skip this match and try the next
                }

                MatchDTO matchDTO = getMatchDTO(player, matchCandidate, meetingPoint);

                Match match = matchService.createMatch(matchDTO);
                log.info("Match found: {} vs {}", player.getPlayer().getUser().getId(),
                        matchCandidate.getPlayer().getUser().getId());
                notifyPlayersAboutMatch(match);
                return match;
            }
        }

        log.warn("No suitable matches found in this iteration");
        throw new MatchmakingException("No suitable matches found in this iteration");
    }

    private MatchDTO getMatchDTO(QueuedPlayer player, QueuedPlayer matchCandidate, double[] meetingPoint) {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setPlayer1Id(player.getPlayer().getProfileId());
        matchDTO.setPlayer2Id(matchCandidate.getPlayer().getProfileId());
        matchDTO.setMeetingLatitude(meetingPoint[0]);
        matchDTO.setMeetingLongitude(meetingPoint[1]);
        matchDTO.setScheduledTime(LocalDateTime.now().plusMinutes(5));
        return matchDTO;
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
     * @param uuid  the UUID of the player to notify
     * @return the match notification
     */
    @Override
    public MatchNotification createMatchNotification(Match match, UUID uuid) {
        String opponentId = match.getPlayer1Id().equals(uuid) ? match.getPlayer2Id().toString()
                : match.getPlayer1Id().toString();
        PlayerProfile opponentProfile = playerProfileService.findByProfileId(opponentId);

        return new MatchNotification(
                match,
                opponentProfile.getName(),
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
        return playerQueue.containsPlayer(playerId);
    }

    /**
     * Prints the current status of the queue.
     */
    @Override
    public void printQueueStatus() {
        log.debug("Current players in queue: {}", playerQueue.size());
        playerQueue.getAllPlayers().forEach(player -> log.debug("Player: {} (ID: {}), Priority: {}, Current Rating: {}",
                player.getPlayer().getUser().getId(),
                player.getPlayer().getProfileId(),
                player.getPriority(),
                player.getPlayer().getCurrentRating()));
        log.debug("--------------------");
    }
}