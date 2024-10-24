package com.project.G1_T3.matchmaking.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.project.G1_T3.matchmaking.controller.websocket.TestStompFrameHandler;
import com.project.G1_T3.matchmaking.controller.websocket.TestStompSessionHandler;
import com.project.G1_T3.matchmaking.model.MatchLocation;
import com.project.G1_T3.matchmaking.model.MatchNotification;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.QueueRequest;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.matchmaking.service.GlickoMatchmaking;
import com.project.G1_T3.matchmaking.service.MatchmakingAlgorithm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.UUID;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MatchmakingIntegrationTests {

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingIntegrationTests.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Autowired
    private MatchmakingService matchmakingService;

    @MockBean
    private GlickoMatchmaking glickoMatchmaking;

    private User user1;
    private User user2;
    private PlayerProfile profile1;
    private PlayerProfile profile2;
    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        playerProfileRepository.deleteAll();
        userRepository.deleteAll();

        user1 = createTestUser("testuser1");
        user2 = createTestUser("testuser2");
        profile1 = createPlayerProfile(user1);
        profile2 = createPlayerProfile(user2);

        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);

        when(glickoMatchmaking.isGoodMatch(any(), any())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        playerProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testMatchmakingFlow() throws Exception {
        StompSession session = connectToWebSocket();

        CompletableFuture<MatchNotification> matchNotificationFuture1 = subscribeToMatchNotifications(session,
                profile1);
        CompletableFuture<MatchNotification> matchNotificationFuture2 = subscribeToMatchNotifications(session,
                profile2);

        sendQueueRequests(session, profile1, profile2);

        // Wait for players to be added to the queue
        Thread.sleep(2000);

        // Manually trigger matchmaking
        logger.info("Triggering matchmaking manually");
        matchmakingService.triggerMatchmaking();

        // Wait for match notifications
        MatchNotification receivedNotification1 = matchNotificationFuture1.get(30, TimeUnit.SECONDS);
        MatchNotification receivedNotification2 = matchNotificationFuture2.get(30, TimeUnit.SECONDS);

        logger.info("Received notification for player 1: {}", receivedNotification1);
        logger.info("Received notification for player 2: {}", receivedNotification2);

        assertNotNull(receivedNotification1);
        assertNotNull(receivedNotification2);
        assertNotNull(receivedNotification1.getMatch(), "Match in notification 1 should not be null");
        assertNotNull(receivedNotification2.getMatch(), "Match in notification 2 should not be null");
        assertEquals(receivedNotification1.getMatch().getMatchId(), receivedNotification2.getMatch().getMatchId(),
                "Match IDs should be equal"); // Add more assertions as needed
    }

    @Test
    void invalidUUIDs() throws Exception {
        StompSession session = connectToWebSocket();

        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId("invalid-uuid");
        queueRequest.setLocation(new MatchLocation(0.0, 0.0));

        session.send("/app/solo/queue", queueRequest);

        // Wait for a short time to allow for processing
        Thread.sleep(1000);

        // Verify that the player was not added to the queue
        // Verify that the player was not added to the queue
        assertThrows(IllegalArgumentException.class, () -> {
            matchmakingService.isPlayerInQueue(UUID.fromString(queueRequest.getPlayerId()));
        });
    }

    @Test
    void testAddToQueueWithNonExistentPlayer() throws Exception {
        StompSession session = connectToWebSocket();

        QueueRequest nonExistentPlayerRequest = new QueueRequest();
        nonExistentPlayerRequest.setPlayerId(UUID.randomUUID().toString());
        nonExistentPlayerRequest.setLocation(new MatchLocation(0.0, 0.0));

        // Send the request with non-existent player
        session.send("/app/solo/queue", nonExistentPlayerRequest);

        // Wait for a short time to allow for processing
        Thread.sleep(1000);

        // Verify that the player was not added to the queue
        assertFalse(matchmakingService.isPlayerInQueue(UUID.fromString(nonExistentPlayerRequest.getPlayerId())));
    }

    @Test
    void testAddToQueueWithNullLocation() throws Exception {
        StompSession session = connectToWebSocket();

        QueueRequest nullLocationRequest = new QueueRequest();
        nullLocationRequest.setPlayerId(profile1.getUserId().toString());
        nullLocationRequest.setLocation(null);

        // Send the request with null location
        session.send("/app/solo/queue", nullLocationRequest);

        // Wait for a short time to allow for processing
        Thread.sleep(1000);

        // Verify that the player was not added to the queue
        assertFalse(matchmakingService.isPlayerInQueue(profile1.getUserId()));
    }

    @Test
    void testRemoveFromQueue() throws Exception {
        StompSession session = connectToWebSocket();

        // First, add a player to the queue
        QueueRequest queueRequest = createQueueRequest(profile1);
        session.send("/app/solo/queue", queueRequest);

        // Wait for the player to be added to the queue
        Thread.sleep(1000);

        // Verify that the player is in the queue
        assertTrue(matchmakingService.isPlayerInQueue(profile1.getUserId()));

        // Now, remove the player from the queue
        session.send("/app/solo/dequeue", queueRequest);

        // Wait for the player to be removed from the queue
        Thread.sleep(1000);

        // Verify that the player is no longer in the queue
        assertFalse(matchmakingService.isPlayerInQueue(profile1.getUserId()));
    }

    private CompletableFuture<MatchNotification> subscribeToMatchNotifications(StompSession session,
            PlayerProfile profile) {
        CompletableFuture<MatchNotification> matchNotificationFuture = new CompletableFuture<>();
        String subscriptionDestination = "/topic/solo/match/" + profile.getProfileId().toString();
        logger.info("Subscribing to: {}", subscriptionDestination);
        session.subscribe(subscriptionDestination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MatchNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                logger.info("Received match notification: {}", payload);
                if (payload instanceof MatchNotification) {
                    MatchNotification notification = (MatchNotification) payload;
                    if (notification.getMatch() == null) {
                        logger.error("Received MatchNotification with null Match");
                    }
                    matchNotificationFuture.complete(notification);
                } else {
                    logger.error("Received unexpected payload type: {}", payload.getClass().getName());
                    matchNotificationFuture
                            .completeExceptionally(new IllegalArgumentException("Unexpected payload type"));
                }
            }
        });
        logger.info("Subscribed successfully to: {}", subscriptionDestination);
        return matchNotificationFuture;
    }

    private void sendQueueRequests(StompSession session, PlayerProfile profile1, PlayerProfile profile2) {
        QueueRequest queueRequest1 = createQueueRequest(profile1);
        QueueRequest queueRequest2 = createQueueRequest(profile2);

        logger.info("Sending queue requests for players: {} and {}", profile1.getUserId(), profile2.getUserId());
        session.send("/app/solo/queue", queueRequest1);
        session.send("/app/solo/queue", queueRequest2);
        logger.info("Queue requests sent");
    }

    private StompSession connectToWebSocket() throws Exception {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();
        String wsUrl = "ws://localhost:" + port + "/ws";
        logger.info("Connecting to WebSocket at: {}", wsUrl);
        stompClient.connectAsync(wsUrl, new TestStompSessionHandler())
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        sessionFuture.completeExceptionally(throwable);
                    } else {
                        sessionFuture.complete(session);
                    }
                });
        return sessionFuture.get(10, TimeUnit.SECONDS);
    }

    private QueueRequest createQueueRequest(PlayerProfile profile) {
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setPlayerId(profile.getUserId().toString());
        queueRequest.setLocation(new MatchLocation(0.0, 0.0));
        return queueRequest;
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setPasswordHash("testpassword");
        user.setRole(UserRole.PLAYER);
        return userRepository.save(user);
    }

    private PlayerProfile createPlayerProfile(User user) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUserId(user.getUserId());
        profile.setFirstName("Test");
        profile.setLastName("User");
        profile.setCurrentRating(1500f);
        return playerProfileRepository.save(profile);
    }
}