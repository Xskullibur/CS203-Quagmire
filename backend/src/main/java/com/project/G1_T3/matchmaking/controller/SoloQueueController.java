package com.project.G1_T3.matchmaking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

import java.util.UUID;

@Controller
public class SoloQueueController {

    private final MatchmakingService matchmakingService;
    private final PlayerProfileRepository playerProfileRepository;

    public SoloQueueController(MatchmakingService matchmakingService, PlayerProfileRepository playerProfileRepository) {
        this.matchmakingService = matchmakingService;
        this.playerProfileRepository = playerProfileRepository;
    }

    @MessageMapping("/solo/queue")
    public void addToQueue(@Payload String playerId, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Received request to join queue for player: " + playerId);
        UUID playerUUID = UUID.fromString(playerId);

        // Add player to queue
        PlayerProfile profile = playerProfileRepository.findByUserId(playerUUID);
        System.out.println("Retrieved profile: " + profile);
        if (profile != null) {
            matchmakingService.addPlayerToQueue(profile);
            System.out.println("Added player to queue: " + profile.getFirstName());
        } else {
            System.out.println("Player profile not found for ID: " + playerId);
            // Handle case where profile is not found
            ResponseEntity.badRequest().body("Player profile not found");
            headerAccessor.getSessionAttributes().put("disconnect", true);
        }
    }

    @MessageMapping("/solo/dequeue")
    public void removeFromQueue(@Payload String playerId) {
        UUID playerUUID = UUID.fromString(playerId);
        matchmakingService.removePlayerFromQueue(playerUUID);
    }
}