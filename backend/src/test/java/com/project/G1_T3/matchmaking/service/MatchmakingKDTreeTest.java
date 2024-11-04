package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MatchmakingKDTreeTest {

    private MatchmakingKDTree kdTree;
    private QueuedPlayer player1;
    private QueuedPlayer player2;
    private QueuedPlayer player3;

    @BeforeEach
    void setUp() {
        kdTree = new MatchmakingKDTree();
        player1 = createQueuedPlayer(1500, 200, 40.7128, -74.0060);
        player2 = createQueuedPlayer(1600, 150, 34.0522, -118.2437);
        player3 = createQueuedPlayer(1700, 100, 51.5074, -0.1278);
    }

    private QueuedPlayer createQueuedPlayer(double rating, double rd, double lat, double lon) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        PlayerProfile profile = new PlayerProfile();
        profile.setUser(user);
        profile.setCurrentRating((float) rating);
        profile.setRatingDeviation((float) rd);
        return new QueuedPlayer(profile, lat, lon);
    }

    @Test
    void testInsertAndSize() {
        assertTrue(kdTree.isEmpty());
        kdTree.insert(player1);
        kdTree.insert(player2);
        assertEquals(2, kdTree.size());
    }

    @Test
    void testRemove() {
        kdTree.insert(player1);
        kdTree.insert(player2);
        kdTree.remove(player1);
        assertEquals(1, kdTree.size());
        assertFalse(kdTree.containsPlayer(player1.getPlayer().getUser().getUserId()));
    }

    @Test
    void testContainsPlayer() {
        kdTree.insert(player1);
        assertTrue(kdTree.containsPlayer(player1.getPlayer().getUser().getUserId()));
        assertFalse(kdTree.containsPlayer(player2.getPlayer().getUser().getUserId()));
    }

    @Test
    void testRemoveByPlayerId() {
        kdTree.insert(player1);
        kdTree.insert(player2);
        kdTree.removeByPlayerId(player1.getPlayer().getUser().getUserId());
        assertEquals(1, kdTree.size());
        assertFalse(kdTree.containsPlayer(player1.getPlayer().getUser().getUserId()));
    }

    @Test
    void testGetAllPlayers() {
        kdTree.insert(player1);
        kdTree.insert(player2);
        List<QueuedPlayer> players = kdTree.getAllPlayers();
        assertEquals(2, players.size());
        assertTrue(players.contains(player1));
        assertTrue(players.contains(player2));
    }

    @Test
    void testPollRootPlayer() {
        kdTree.insert(player1);
        kdTree.insert(player2);
        QueuedPlayer rootPlayer = kdTree.pollRootPlayer();
        assertNotNull(rootPlayer);
        assertEquals(1, kdTree.size());
    }
}
