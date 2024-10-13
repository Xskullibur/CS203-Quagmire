package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.player.model.PlayerProfile;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerQueue {
    private final PriorityBlockingQueue<QueuedPlayer> queue;

    public PlayerQueue() {
        this.queue = new PriorityBlockingQueue<>(11, Comparator
                .comparingDouble(QueuedPlayer::getPriority)
                .reversed()
                .thenComparingLong(QueuedPlayer::getJoinTime));
    }

    public void addPlayer(PlayerProfile player) {
        queue.offer(new QueuedPlayer(player));
    }

    public PlayerProfile pollPlayer() {
        QueuedPlayer queuedPlayer = queue.poll();
        return queuedPlayer != null ? queuedPlayer.getPlayer() : null;
    }

    public int size() {
        return queue.size();
    }

    // TODO: to move this out to the model/ directory
    private static class QueuedPlayer {
        private final PlayerProfile player;
        private final long joinTime;

        public QueuedPlayer(PlayerProfile player) {
            this.player = player;
            this.joinTime = System.currentTimeMillis();
        }

        public PlayerProfile getPlayer() {
            return player;
        }

        public long getJoinTime() {
            return joinTime;
        }

        public double getPriority() {
            long waitTime = System.currentTimeMillis() - joinTime;
            return player.getCurrentRating() + (waitTime / 1000.0);
        }
    }
}