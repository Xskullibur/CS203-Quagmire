package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerQueueImpl implements PlayerQueue {
    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerQueueImpl() {
        this.queue = new PriorityBlockingQueue<>(11, Comparator
                .comparingDouble(QueuedPlayer::getPriority)
                .reversed()
                .thenComparing(QueuedPlayer::getJoinTime));
    }

    @Override
    public void addPlayer(PlayerProfile player, double latitude, double longitude) {
        queue.offer(new QueuedPlayer(player, latitude, longitude));
    }

    @Override
    public PlayerProfile pollPlayer() {
        QueuedPlayer queuedPlayer = queue.poll();
        return queuedPlayer != null ? queuedPlayer.getPlayer() : null;
    }

    @Override
    public int size() {
        return queue.size();
    }
}