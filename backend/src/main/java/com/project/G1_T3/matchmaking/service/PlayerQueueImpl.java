package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Implementation of the PlayerQueue interface using a PriorityBlockingQueue.
 * This class manages a queue of players, prioritizing them based on their priority and join time.
 */
@Component
public class PlayerQueueImpl implements PlayerQueue {
    private final PriorityBlockingQueue<QueuedPlayer> queue;

    /**
     * Constructor initializes the priority queue with a custom comparator.
     * The comparator prioritizes players based on their priority (higher is better),
     * and in case of a tie, based on their join time (earlier is better).
     */
    public PlayerQueueImpl() {
        this.queue = new PriorityBlockingQueue<>(11, Comparator
                .comparingDouble(QueuedPlayer::getPriority)
                .reversed()
                .thenComparing(QueuedPlayer::getJoinTime));
    }

    /**
     * Adds a player to the queue with the specified latitude and longitude.
     *
     * @param player    the player profile to be added
     * @param latitude  the latitude of the player's location
     * @param longitude the longitude of the player's location
     */
    @Override
    public void addPlayer(PlayerProfile player, double latitude, double longitude) {
        queue.offer(new QueuedPlayer(player, latitude, longitude));
    }

    /**
     * Polls the highest priority player from the queue.
     *
     * @return the player profile of the polled player, or null if the queue is empty
     */
    @Override
    public PlayerProfile pollPlayer() {
        QueuedPlayer queuedPlayer = queue.poll();
        return queuedPlayer != null ? queuedPlayer.getPlayer() : null;
    }

    /**
     * Returns the current size of the queue.
     *
     * @return the number of players in the queue
     */
    @Override
    public int size() {
        return queue.size();
    }
}