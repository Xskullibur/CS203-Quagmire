package com.project.G1_T3.matchmaking.controller.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import com.project.G1_T3.match.model.Match;

public class TestStompFrameHandler implements StompFrameHandler {

    private CompletableFuture<Match> completableFuture;

    public TestStompFrameHandler(CompletableFuture<Match> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Match.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload instanceof Match) {
            completableFuture.complete((Match) payload);
        } else {
            completableFuture.completeExceptionally(new IllegalArgumentException("Unexpected payload type"));
        }
    }
}
