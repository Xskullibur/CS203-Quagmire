package com.project.G1_T3.playerprofile.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.repository.PlayerProfileRepository;
import jakarta.annotation.PostConstruct;

@Service
public class PlayerRatingService {

    private static final int MAX_RATING = 3000;

    private final int[] bucketCounts = new int[MAX_RATING + 1];
    private final int[] prefixSums = new int[MAX_RATING + 1];

    @SuppressWarnings("unchecked")
    private final Set<UUID>[] ratingBuckets = new HashSet[MAX_RATING + 1];

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @PostConstruct
    public void initializeBuckets() {
        lock.writeLock().lock();
        try {
            // Initialize each Set in the array
            for (int i = 0; i <= MAX_RATING; i++) {
                ratingBuckets[i] = new HashSet<>();
            }

            List<PlayerProfile> players = playerProfileRepository.findAll();
            for (PlayerProfile player : players) {
                int rating = Math.round(player.getGlickoRating());
                UUID playerId = player.getProfileId(); // Assuming PlayerProfile has getProfileId() method that returns
                                                       // UUID

                // Add player ID to the bucket for their rating
                ratingBuckets[rating].add(playerId);
                bucketCounts[rating]++;
            }
            computePrefixSums();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Scheduled(fixedRate = 60000) // Refresh every minute
    public void refreshData() {
        initializeBuckets();
    }

    public int[] getBucketCounts() {
        return bucketCounts;
    }

    public int[] getPrefixSums() {
        return prefixSums;
    }

    public int getTotalPlayers() {
        return prefixSums[0];
    }

    public Set<UUID>[] getRatingBuckets() {
        return ratingBuckets;
    }

    private void computePrefixSums() {
        lock.writeLock().lock();
        try {
            prefixSums[MAX_RATING] = bucketCounts[MAX_RATING];
            for (int i = MAX_RATING - 1; i >= 0; i--) {
                prefixSums[i] = bucketCounts[i] + prefixSums[i + 1];
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getNumberOfPlayersAhead(int rating) {
        lock.readLock().lock();
        try {
            if (rating + 1 <= MAX_RATING) {
                return prefixSums[rating + 1];
            } else {
                return 0;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getNumberOfPlayersInBucket(int rating) {
        lock.readLock().lock();
        try {
            if (rating <= MAX_RATING) {
                return bucketCounts[rating];
            } else {
                return 0;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addPlayer(UUID playerId, int rating) {
        lock.writeLock().lock();
        try {
            ratingBuckets[rating].add(playerId);
            bucketCounts[rating]++;
            for (int i = rating; i >= 0; i--) {
                prefixSums[i] = bucketCounts[i] + ((i + 1 <= MAX_RATING) ? prefixSums[i + 1] : 0);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deletePlayer(UUID playerId, int rating) {
        lock.writeLock().lock();
        try {
            // Check if the player exists in the rating bucket
            if (ratingBuckets[rating].remove(playerId)) {
                // Decrease the bucket count
                bucketCounts[rating]--;

                // Update prefix sums
                for (int i = rating; i >= 0; i--) {
                    prefixSums[i] = bucketCounts[i] + ((i + 1 <= MAX_RATING) ? prefixSums[i + 1] : 0);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateRating(UUID playerId, int oldRating, int newRating) {
        lock.writeLock().lock();
        try {
            // Remove player from old rating bucket
            ratingBuckets[oldRating].remove(playerId);
            bucketCounts[oldRating]--;

            // Add player to new rating bucket
            ratingBuckets[newRating].add(playerId);
            bucketCounts[newRating]++;

            // Update prefix sums from the minimum of old and new ratings
            int startBucket = Math.max(oldRating, newRating);
            for (int i = startBucket; i >= 0; i--) {
                prefixSums[i] = bucketCounts[i] + ((i + 1 <= MAX_RATING) ? prefixSums[i + 1] : 0);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<UUID> getTop10Players() {
        lock.readLock().lock();
        try {
            List<UUID> topPlayers = new ArrayList<>();
            for (int rating = MAX_RATING; rating >= 0 && topPlayers.size() < 10; rating--) {
                Set<UUID> bucket = ratingBuckets[rating];
                if (bucket != null && !bucket.isEmpty()) {
                    topPlayers.addAll(bucket);
                }
            }
            return topPlayers.stream().collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<UUID> getPlayersInBucket(int rating) {
        return ratingBuckets[rating];
    }
}
