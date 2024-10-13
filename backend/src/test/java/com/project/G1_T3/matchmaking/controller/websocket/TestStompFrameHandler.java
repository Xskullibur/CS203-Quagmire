package com.project.G1_T3.matchmaking.controller.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import com.project.G1_T3.match.model.Match;

public class TestStompFrameHandler implements StompFrameHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestStompFrameHandler.class);
    private final CompletableFuture<Match> future;

    public TestStompFrameHandler(CompletableFuture<Match> future) {
        this.future = future;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        logger.info("getPayloadType called with headers: {}", headers);
        return Match.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("handleFrame called with headers: {} and payload: {}", headers, payload);
        future.complete((Match) payload);
    }
}