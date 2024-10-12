package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Slf4j
@Service
public class MatchmakingServiceImpl implements MatchmakingService {
    private final ConcurrentMap<UUID, QueuedPlayer> playerQueue = new ConcurrentHashMap<>();
    private final MatchmakingAlgorithm matchmakingAlgorithm;
    private final MeetingPointService meetingPointService;
    private final MatchService matchService;
    private final SimpMessagingTemplate messagingTemplate;

    public MatchmakingServiceImpl(MatchmakingAlgorithm matchmakingAlgorithm,
            MeetingPointService meetingPointService,
            MatchService matchService,
            SimpMessagingTemplate messagingTemplate) {
        this.matchmakingAlgorithm = matchmakingAlgorithm;
        this.meetingPointService = meetingPointService;
        this.matchService = matchService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void addPlayerToQueue(PlayerProfile player, double latitude, double longitude) {
        log.info("Adding player to queue: {} (ID: {})", player.getUserId(), player.getProfileId());
        QueuedPlayer queuedPlayer = new QueuedPlayer(player, latitude, longitude);
        playerQueue.put(player.getUserId(), queuedPlayer);
        log.info("Player added. Current queue size: {}", playerQueue.size());
        if (playerQueue.size() >= 2) {
            triggerMatchmaking();
        }
    }

    @Override
    public void removePlayerFromQueue(UUID playerId) {
        log.info("Removing player from queue: {}", playerId);
        if (!playerQueue.containsKey(playerId)) {
            throw new PlayerNotFoundException("Player with ID " + playerId + " not found in queue");
        }
        playerQueue.remove(playerId);
        log.info("Player removed. Current queue size: {}", playerQueue.size());
    }

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

                // Create a MatchDTO and use MatchService to create the match
                MatchDTO matchDTO = new MatchDTO();
                matchDTO.setPlayer1Id(player1.getPlayer().getProfileId());
                matchDTO.setPlayer2Id(player2.getPlayer().getProfileId());
                matchDTO.setMeetingLatitude(meetingPoint[0]);
                matchDTO.setMeetingLongitude(meetingPoint[1]);

                Match match = matchService.createMatch(matchDTO);
                log.info("Match found: {} vs {}", player1.getPlayer().getUserId(), player2.getPlayer().getUserId());
                notifyPlayers(match);
                return match;
            }
        }
        log.warn("No suitable match found in this iteration");
        throw new MatchmakingException("No suitable match found in this iteration");
    }

    private void notifyPlayers(Match match) {
        log.info("Notifying players about the match: {}", match.getMatchId());
        messagingTemplate.convertAndSendToUser(match.getPlayer1Id().toString(), "/queue/matches", match);
        messagingTemplate.convertAndSendToUser(match.getPlayer2Id().toString(), "/queue/matches", match);
    }

    public void triggerMatchmaking() {
        log.info("Triggering matchmaking process");
        try {
            findMatch();
        } catch (MatchmakingException e) {
            log.info("Matchmaking process completed without finding a match: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during matchmaking process", e);
        }
    }

    public boolean isPlayerInQueue(UUID playerId) {
        return playerQueue.containsKey(playerId);
    }

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