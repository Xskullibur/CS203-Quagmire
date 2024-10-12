package com.project.G1_T3.matchmaking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.matchmaking.model.QueueRequest;
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
    public void addToQueue(@Payload QueueRequest queueRequest, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UUID playerUUID = UUID.fromString(queueRequest.getPlayerId());
            PlayerProfile profile = playerProfileRepository.findByUserId(playerUUID);
            if (profile != null) {
                if (queueRequest.getLocation() != null) {
                    matchmakingService.addPlayerToQueue(profile,
                            queueRequest.getLocation().getLatitude(),
                            queueRequest.getLocation().getLongitude());
                } else {
                    System.out.println("Location is null for player ID: " + queueRequest.getPlayerId());
                    ResponseEntity.badRequest().body("Location is required");
                    setDisconnectAttribute(headerAccessor);
                }
            } else {
                System.out.println("Player profile not found for ID: " + queueRequest.getPlayerId());
                ResponseEntity.badRequest().body("Player profile not found");
                setDisconnectAttribute(headerAccessor);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID: " + queueRequest.getPlayerId());
            ResponseEntity.badRequest().body("Invalid player ID");
            setDisconnectAttribute(headerAccessor);
        }
    }

    private void setDisconnectAttribute(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("disconnect", true);
        }
    }

    @MessageMapping("/solo/dequeue")
    public void removeFromQueue(@Payload QueueRequest queueRequest) {
        try {
            UUID playerUUID = UUID.fromString(queueRequest.getPlayerId());
            matchmakingService.removePlayerFromQueue(playerUUID);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID for dequeue: " + queueRequest.getPlayerId());
            // Handle invalid UUID (e.g., send an error message back to the client)
        }
    }
}