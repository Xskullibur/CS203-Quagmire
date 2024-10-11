package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public MatchmakingServiceImpl(MatchmakingAlgorithm matchmakingAlgorithm, MeetingPointService meetingPointService) {
        this.matchmakingAlgorithm = matchmakingAlgorithm;
        this.meetingPointService = meetingPointService;
    }

    @Override
    public void addPlayerToQueue(PlayerProfile player, double latitude, double longitude) {
        log.info("Adding player to queue: {} (ID: {})", player.getUserId(), player.getProfileId());
        QueuedPlayer queuedPlayer = new QueuedPlayer(player, latitude, longitude);
        playerQueue.put(player.getUserId(), queuedPlayer);
        log.info("Player added. Current queue size: {}", playerQueue.size());
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
        if (playerQueue.size() < 2) {
            throw new InsufficientPlayersException("Not enough players in queue for matching");
        }
        List<QueuedPlayer> players = new ArrayList<>(playerQueue.values());
        players.sort(Comparator.comparingDouble(QueuedPlayer::getPriority).reversed());
        for (int i = 0; i < players.size() - 1; i++) {
            QueuedPlayer player1 = players.get(i);
            QueuedPlayer player2 = players.get(i + 1);
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
                Match match = new Match();
                match.setGameType(Match.GameType.SOLO);
                match.setPlayer1Id(player1.getPlayer().getProfileId());
                match.setPlayer2Id(player2.getPlayer().getProfileId());
                match.setStatus(Status.SCHEDULED);
                match.setMeetingLatitude(meetingPoint[0]);
                match.setMeetingLongitude(meetingPoint[1]);
                log.info("Match found: {} vs {}", player1.getPlayer().getUserId(), player2.getPlayer().getUserId());
                return match;
            }
        }
        throw new MatchmakingException("No suitable match found in this iteration");
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