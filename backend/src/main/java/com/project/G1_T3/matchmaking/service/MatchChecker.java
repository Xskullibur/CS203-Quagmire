package com.project.G1_T3.matchmaking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

@Component
public class MatchChecker {

    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerProfileRepository playerProfileRepository;

    @Autowired
    public MatchChecker(MatchmakingService matchmakingService, SimpMessagingTemplate messagingTemplate,
            PlayerProfileRepository playerProfileRepository) {
        this.matchmakingService = matchmakingService;
        this.messagingTemplate = messagingTemplate;
        this.playerProfileRepository = playerProfileRepository;
    }

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void checkForMatches() {
        // Print queue status before checking for matches
        matchmakingService.printQueueStatus();

        Match match = matchmakingService.findMatch();
        if (match != null) {
            PlayerProfile player1 = playerProfileRepository.findByUserId(match.getPlayer1Id());
            PlayerProfile player2 = playerProfileRepository.findByUserId(match.getPlayer2Id());

            // Create custom messages for each player
            MatchNotification notificationForPlayer1 = new MatchNotification(match, player2.getFirstName());
            MatchNotification notificationForPlayer2 = new MatchNotification(match, player1.getFirstName());

            // Send personalized notifications to players
            messagingTemplate.convertAndSend("/topic/solo/match/" + match.getPlayer1Id(), notificationForPlayer1);
            messagingTemplate.convertAndSend("/topic/solo/match/" + match.getPlayer2Id(), notificationForPlayer2);

            System.out.println("Match found: " + player1.getFirstName() + " vs " + player2.getFirstName());
        }
    }

    // scheduled method to print queue status periodically
    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void printQueueStatus() {
        matchmakingService.printQueueStatus();
    }
}