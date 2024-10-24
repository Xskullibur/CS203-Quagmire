package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
class MatchmakingKDTree {
    private Node root;
    private static final int K = 4; // 4D KD-Tree (glickoRating, glickoRD, latitude, longitude)
    private int size;

    private class Node {
        QueuedPlayer player;
        Node left, right;
        int depth;

        Node(QueuedPlayer player, int depth) {
            this.player = player;
            this.depth = depth;
            this.left = null;
            this.right = null;
        }
    }

    public MatchmakingKDTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(QueuedPlayer player) {
        root = insert(root, player, 0);
        size++;
    }

    private Node insert(Node node, QueuedPlayer player, int depth) {
        if (node == null) {
            return new Node(player, depth);
        }

        int axis = depth % K;
        double value = getAxisValue(player, axis);
        double nodeValue = getAxisValue(node.player, axis);

        if (value < nodeValue) {
            node.left = insert(node.left, player, depth + 1);
        } else {
            node.right = insert(node.right, player, depth + 1);
        }

        return node;
    }

    private double getAxisValue(QueuedPlayer player, int axis) {
        switch (axis) {
            case 0:
                return player.getGlickoRating();
            case 1:
                return player.getGlickoRD();
            case 2:
                return player.getLatitude();
            case 3:
                return player.getLongitude();
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    public PriorityQueue<Map.Entry<QueuedPlayer, Double>> findKNearest(QueuedPlayer target, double maxRatingDiff,
            double maxDeviationDiff, double maxDistanceKm, int k) {
        PriorityQueue<Map.Entry<QueuedPlayer, Double>> nearestNeighbors = new PriorityQueue<>(
                Comparator.<Map.Entry<QueuedPlayer, Double>>comparingDouble(Map.Entry::getValue).reversed());
        findKNearest(root, target, maxRatingDiff, maxDeviationDiff, maxDistanceKm, k, nearestNeighbors);
        return nearestNeighbors;
    }

    private void findKNearest(Node node, QueuedPlayer target, double maxRatingDiff, double maxDeviationDiff,
            double maxDistanceKm, int k, PriorityQueue<Map.Entry<QueuedPlayer, Double>> nearestNeighbors) {
        if (node == null)
            return;

        double distance = calculateDistance(target, node.player);
        if (distance <= maxDistanceKm) {
            nearestNeighbors.offer(Map.entry(node.player, distance));
            if (nearestNeighbors.size() > k) {
                nearestNeighbors.poll();
            }
        }

        int axis = node.depth % K;
        double targetValue = getAxisValue(target, axis);
        double nodeValue = getAxisValue(node.player, axis);

        Node next = (targetValue < nodeValue) ? node.left : node.right;
        Node other = (next == node.left) ? node.right : node.left;

        findKNearest(next, target, maxRatingDiff, maxDeviationDiff, maxDistanceKm, k, nearestNeighbors);
        if (Math.abs(targetValue - nodeValue) < maxDistanceKm) {
            findKNearest(other, target, maxRatingDiff, maxDeviationDiff, maxDistanceKm, k, nearestNeighbors);
        }
    }

    private double calculateDistance(QueuedPlayer a, QueuedPlayer b) {
        double ratingDiff = a.getGlickoRating() - b.getGlickoRating();
        double rdDiff = a.getGlickoRD() - b.getGlickoRD();
        double latDiff = a.getLatitude() - b.getLatitude();
        double lonDiff = a.getLongitude() - b.getLongitude();
        return Math.sqrt(ratingDiff * ratingDiff + rdDiff * rdDiff + latDiff * latDiff + lonDiff * lonDiff);
    }

    public void remove(QueuedPlayer player) {
        root = remove(root, player, 0);
        size--;
    }

    private Node remove(Node node, QueuedPlayer player, int depth) {
        if (node == null)
            return null;

        int axis = depth % K;
        double value = getAxisValue(player, axis);
        double nodeValue = getAxisValue(node.player, axis);

        if (player.equals(node.player)) {
            // Node deletion logic (find a replacement node if needed)
            if (node.right != null) {
                Node min = findMin(node.right, axis, depth + 1);
                node.player = min.player;
                node.right = remove(node.right, min.player, depth + 1);
            } else if (node.left != null) {
                Node min = findMin(node.left, axis, depth + 1);
                node.player = min.player;
                node.right = remove(node.left, min.player, depth + 1);
                node.left = null;
            } else {
                return null;
            }
        } else if (value < nodeValue) {
            node.left = remove(node.left, player, depth + 1);
        } else {
            node.right = remove(node.right, player, depth + 1);
        }
        return node;
    }

    private Node findMin(Node node, int axis, int depth) {
        if (node == null)
            return null;

        int currentAxis = depth % K;
        if (currentAxis == axis) {
            if (node.left == null) {
                return node;
            }
            return findMin(node.left, axis, depth + 1);
        }

        Node leftMin = findMin(node.left, axis, depth + 1);
        Node rightMin = findMin(node.right, axis, depth + 1);

        Node min = node;
        if (leftMin != null && getAxisValue(leftMin.player, axis) < getAxisValue(min.player, axis)) {
            min = leftMin;
        }
        if (rightMin != null && getAxisValue(rightMin.player, axis) < getAxisValue(min.player, axis)) {
            min = rightMin;
        }
        return min;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public QueuedPlayer pollRootPlayer() {
        if (root == null) {
            return null;
        }
        QueuedPlayer player = root.player;
        remove(player);
        return player;
    }

    public boolean containsPlayer(UUID playerId) {
        return containsPlayer(root, playerId);
    }

    private boolean containsPlayer(Node node, UUID playerId) {
        if (node == null) {
            return false;
        }
        if (node.player.getPlayer().getUserId().equals(playerId)) {
            return true;
        }
        return containsPlayer(node.left, playerId) || containsPlayer(node.right, playerId);
    }

    public void removeByPlayerId(UUID playerId) {
        removeByPlayerId(root, playerId, 0);
    }

    private void removeByPlayerId(Node node, UUID playerId, int depth) {
        if (node == null) {
            return;
        }
        if (node.player.getPlayer().getUserId().equals(playerId)) {
            remove(node.player);
        } else {
            removeByPlayerId(node.left, playerId, depth + 1);
            removeByPlayerId(node.right, playerId, depth + 1);
        }
    }

    public List<QueuedPlayer> getAllPlayers() {
        List<QueuedPlayer> players = new ArrayList<>();
        getAllPlayers(root, players);
        return players;
    }

    private void getAllPlayers(Node node, List<QueuedPlayer> players) {
        if (node == null) {
            return;
        }
        players.add(node.player);
        getAllPlayers(node.left, players);
        getAllPlayers(node.right, players);
    }
}
