package com.project.G1_T3.matchmaking.model;

import com.project.G1_T3.player.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class QueuedPlayerTest {

    private PlayerProfile mockProfile;
    private QueuedPlayer queuedPlayer;

    @BeforeEach
    void setUp() {
        mockProfile = mock(PlayerProfile.class);
        when(mockProfile.getCurrentRating()).thenReturn((float) 1000.0);
        queuedPlayer = new QueuedPlayer(mockProfile, 10, 20);
    }

    @Test
    void constructor_shouldSetAllFields() {
        assertEquals(mockProfile, queuedPlayer.getPlayer());
        assertEquals(10, queuedPlayer.getLatitude(), 0.001);
        assertEquals(20, queuedPlayer.getLongitude(), 0.001);
        assertNotNull(queuedPlayer.getJoinTime());
    }

    @Test
    void getQueueTimeSeconds_shouldReturnPositiveValue() {
        assertTrue(queuedPlayer.getQueueTimeSeconds() >= 0);
    }

    @Test
    void getPriority_shouldCombineWaitTimeAndRating() throws InterruptedException {
        double initialPriority = queuedPlayer.getPriority();
        Thread.sleep(1000); // Wait for 1 second
        double laterPriority = queuedPlayer.getPriority();
        assertTrue(laterPriority > initialPriority);
    }

    @Test
    void getPriority_zeroWaitTime_shouldEqualRating() {
        QueuedPlayer newPlayer = new QueuedPlayer(mockProfile, 0, 0) {
            @Override
            public long getQueueTimeSeconds() {
                return 0;
            }
        };
        assertEquals(1000.0, newPlayer.getPriority(), 0.001);
    }

    @Test
    void getPriority_longWaitTime_shouldIncreasePriority() {
        QueuedPlayer longWaitPlayer = new QueuedPlayer(mockProfile, 0, 0) {
            @Override
            public long getQueueTimeSeconds() {
                return 3600; // 1 hour wait
            }
        };
        assertTrue(longWaitPlayer.getPriority() > 1000.0);
    }
}