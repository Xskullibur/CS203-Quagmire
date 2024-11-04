package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerQueueImplTest {

    private PlayerQueue playerQueue;

    @BeforeEach
    void setUp() {
        playerQueue = new PlayerQueueImpl();
    }

    @Test
    void addPlayer_shouldIncreaseSize() {
        PlayerProfile mockProfile = mock(PlayerProfile.class);
        when(mockProfile.getGlickoRating()).thenReturn((float) 1000.0);

        playerQueue.addPlayer(mockProfile, 0, 0);
        assertEquals(1, playerQueue.size());
    }

    @Test
    void pollPlayer_emptyQueue_shouldReturnNull() {
        assertNull(playerQueue.pollPlayer());
    }

    @Test
    void pollPlayer_shouldDecreaseSize() {
        PlayerProfile mockProfile = mock(PlayerProfile.class);
        when(mockProfile.getGlickoRating()).thenReturn((float) 1000.0);

        playerQueue.addPlayer(mockProfile, 0, 0);
        assertNotNull(playerQueue.pollPlayer());
        assertEquals(0, playerQueue.size());
    }

    @Test
    void pollPlayer_shouldReturnPlayersInPriorityOrder() throws InterruptedException {
        PlayerProfile highPriority = mock(PlayerProfile.class);
        when(highPriority.getGlickoRating()).thenReturn((float) 2000.0);

        PlayerProfile lowPriority = mock(PlayerProfile.class);
        when(lowPriority.getGlickoRating()).thenReturn((float) 1000.0);

        playerQueue.addPlayer(lowPriority, 0, 0);
        Thread.sleep(100); // Ensure different join times
        playerQueue.addPlayer(highPriority, 0, 0);

        assertEquals(highPriority, playerQueue.pollPlayer());
        assertEquals(lowPriority, playerQueue.pollPlayer());
    }

    @Test
    void size_shouldReturnCorrectSize() {
        assertEquals(0, playerQueue.size());

        PlayerProfile mockProfile = mock(PlayerProfile.class);
        when(mockProfile.getGlickoRating()).thenReturn((float) 1000.0);

        playerQueue.addPlayer(mockProfile, 0, 0);
        assertEquals(1, playerQueue.size());

        playerQueue.addPlayer(mockProfile, 0, 0);
        assertEquals(2, playerQueue.size());

        playerQueue.pollPlayer();
        assertEquals(1, playerQueue.size());
    }
}
