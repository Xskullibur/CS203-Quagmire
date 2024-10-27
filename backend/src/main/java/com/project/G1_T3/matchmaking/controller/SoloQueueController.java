package com.project.G1_T3.matchmaking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import com.project.G1_T3.matchmaking.model.QueueRequest;
import java.util.UUID;


@Controller
public class SoloQueueController {
    private final MatchmakingService matchmakingService;
    private final PlayerProfileRepository playerProfileRepository;

    /**
     * Controller for handling solo queue matchmaking operations.
     * This controller interacts with the matchmaking service and player profile repository
     * to manage solo queue matchmaking functionalities.
     *
     * @param matchmakingService the service responsible for matchmaking operations
     * @param playerProfileRepository the repository for accessing player profiles
     */
    public SoloQueueController(MatchmakingService matchmakingService, PlayerProfileRepository playerProfileRepository) {
        this.matchmakingService = matchmakingService;
        this.playerProfileRepository = playerProfileRepository;
    }

    /**
     * Handles the addition of a player to the solo queue.
     * 
     * @param queueRequest the request containing player ID and location information
     * @param headerAccessor the accessor for message headers
     * 
     * @MessageMapping("/solo/queue")
     * 
     * This method processes a request to add a player to the matchmaking solo queue.
     * It retrieves the player's profile using the provided player ID and, if the profile
     * is found and the location is provided, adds the player to the matchmaking queue.
     * If the player profile is not found or the location is missing, it logs an error
     * message and sets the disconnect attribute in the header accessor.
     * 
     * @throws IllegalArgumentException if the provided player ID is not a valid UUID
     */
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

    /**
     * Sets the "disconnect" attribute in the session attributes of the provided
     * SimpMessageHeaderAccessor to true. This attribute can be used to indicate
     * that the session has been disconnected.
     *
     * @param headerAccessor the SimpMessageHeaderAccessor containing the session
     *                       attributes to be modified. If the session attributes
     *                       are null, no action is taken.
     */
    private void setDisconnectAttribute(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("disconnect", true);
        }
    }

    /**
     * Handles the removal of a player from the solo queue.
     * 
     * This method is mapped to the "/solo/dequeue" message endpoint and is triggered
     * when a client sends a message to this endpoint. It attempts to remove the player
     * from the matchmaking queue based on the provided player ID.
     * 
     * @param queueRequest the request payload containing the player ID to be removed from the queue
     * @throws IllegalArgumentException if the provided player ID is not a valid UUID
     */
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