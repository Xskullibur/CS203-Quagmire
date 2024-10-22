package com.project.G1_T3.player.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

import jakarta.annotation.PostConstruct;

@Service
public class PlayerRatingService {

    private static final int MAX_RATING = 3000;

    private final int[] bucketCounts = new int[MAX_RATING + 1];
    private final int[] prefixSums = new int[MAX_RATING + 1];

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @PostConstruct
    public void initializeBuckets() {
        // Initialize bucketCounts
        List<Object[]> results = playerProfileRepository.getRatingCounts();
        for (Object[] row : results) {
            int rating = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            bucketCounts[rating] = (int) count;
        }

        // Compute prefix sums
        computePrefixSums();
    }

    public int[] getBucketCounts(){
        return bucketCounts;
    }

    public int[] getPrefixSums(){
        return prefixSums;
    }

    public int getTotalPlayers(){
        return prefixSums[0];
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

    public void addPlayer(int rating) {
        lock.writeLock().lock();
        try {
            // Update bucket counts
            bucketCounts[rating] += 1;

            // Update prefix sums starting from the rating
            for (int i = rating; i >= 0; i--) {
                prefixSums[i] = bucketCounts[i] + ((i + 1 <= MAX_RATING) ? prefixSums[i + 1] : 0);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateRating(int oldRating, int newRating) {
        lock.writeLock().lock();
        try {
            // Update bucket counts
            bucketCounts[oldRating] -= 1;
            bucketCounts[newRating] += 1;

            // Update prefix sums from the minimum of old and new ratings
            int startBucket = Math.max(oldRating, newRating);
            for (int i = startBucket; i >= 0; i--) {
                prefixSums[i] = bucketCounts[i] + ((i + 1 <= MAX_RATING) ? prefixSums[i + 1] : 0);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
