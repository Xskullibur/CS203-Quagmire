package com.project.G1_T3.matchmaking.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.project.G1_T3.matchmaking.controller.websocket.TestStompFrameHandler;
import com.project.G1_T3.matchmaking.controller.websocket.TestStompSessionHandler;
import com.project.G1_T3.matchmaking.model.MatchLocation;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.matchmaking.model.QueueRequest;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.matchmaking.service.MatchmakingService;
import com.project.G1_T3.matchmaking.service.MatchmakingAlgorithm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
    private MatchmakingAlgorithm matchmakingAlgorithm;

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
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        when(matchmakingAlgorithm.isGoodMatch(any(), any())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        playerProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testMatchmakingFlow() throws Exception {
        StompSessionHandler sessionHandler = new TestStompSessionHandler();
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        String wsUrl = "ws://localhost:" + port + "/ws";
        logger.info("Connecting to WebSocket at: {}", wsUrl);
        stompClient.connectAsync(wsUrl, sessionHandler)
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        sessionFuture.completeExceptionally(throwable);
                    } else {
                        sessionFuture.complete(session);
                    }
                });

        StompSession session = sessionFuture.get(30, TimeUnit.SECONDS);
        logger.info("WebSocket connection established");

        CompletableFuture<Match> matchNotificationFuture1 = new CompletableFuture<>();
        CompletableFuture<Match> matchNotificationFuture2 = new CompletableFuture<>();

        String subscriptionDestination1 = "/user/" + profile1.getUserId() + "/queue/matches";
        String subscriptionDestination2 = "/user/" + profile2.getUserId() + "/queue/matches";
        logger.info("Subscribing to: {} and {}", subscriptionDestination1, subscriptionDestination2);
        session.subscribe(subscriptionDestination1, new TestStompFrameHandler(matchNotificationFuture1));
        session.subscribe(subscriptionDestination2, new TestStompFrameHandler(matchNotificationFuture2));
        Thread.sleep(1000); // Wait for subscriptions to be fully established
        logger.info("Subscribed to match notifications");

        QueueRequest queueRequest1 = new QueueRequest();
        queueRequest1.setPlayerId(profile1.getUserId().toString());
        queueRequest1.setLocation(new MatchLocation(0, 0));

        QueueRequest queueRequest2 = new QueueRequest();
        queueRequest2.setPlayerId(profile2.getUserId().toString());
        queueRequest2.setLocation(new MatchLocation(0.1, 0.1));

        logger.info("Sending queue requests for players: {} and {}", profile1.getUserId(), profile2.getUserId());
        session.send("/app/solo/queue", queueRequest1);
        session.send("/app/solo/queue", queueRequest2);
        logger.info("Queue requests sent");

        // Wait for players to be added to the queue
        Thread.sleep(2000);

        // Manually trigger matchmaking
        logger.info("Triggering matchmaking manually");
        matchmakingService.triggerMatchmaking();
        logger.info("Matchmaking triggered manually");

        Match match1 = null;
        Match match2 = null;
        try {
            match1 = matchNotificationFuture1.get(15, TimeUnit.SECONDS);
            match2 = matchNotificationFuture2.get(15, TimeUnit.SECONDS);
            logger.info("Received match notifications for both players");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Failed to receive match notifications", e);
            fail("Did not receive match notifications in time: " + e.getMessage());
        }

        assertNotNull(match1, "Match 1 should not be null");
        assertNotNull(match2, "Match 2 should not be null");
        assertEquals(match1.getMatchId(), match2.getMatchId(), "Match IDs should be equal");

        ResponseEntity<Match> matchResponse = restTemplate.getForEntity("/matches/current/" + user1.getUserId(),
                Match.class);
        assertEquals(HttpStatus.OK, matchResponse.getStatusCode(), "HTTP status should be OK");
        Match retrievedMatch = matchResponse.getBody();
        assertNotNull(retrievedMatch, "Retrieved match should not be null");
        assertEquals(match1.getMatchId(), retrievedMatch.getMatchId(),
                "Retrieved match ID should match the original match ID");
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
        profile.setRating(1500f);
        return playerProfileRepository.save(profile);
    }
}