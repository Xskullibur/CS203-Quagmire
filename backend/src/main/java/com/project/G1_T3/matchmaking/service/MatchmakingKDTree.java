package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;

import java.util.*;

import org.springframework.stereotype.Component;

/**
 * The MatchmakingKDTree class represents a k-dimensional tree (KD-Tree) for
 * matchmaking purposes.
 * It supports insertion, deletion, and nearest neighbor search operations based
 * on multiple dimensions:
 * glickoRating, glickoRD, latitude, and longitude.
 *
 * <p>
 * This class is designed to facilitate efficient matchmaking by organizing
 * players in a multi-dimensional space.
 * It provides methods to insert players, find the k-nearest neighbors, remove
 * players, and retrieve all players.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * MatchmakingKDTree kdTree = new MatchmakingKDTree();
 * kdTree.insert(new QueuedPlayer(...));
 * PriorityQueue<Map.Entry<QueuedPlayer, Double>> nearestNeighbors = kdTree.findKNearest(targetPlayer, maxRatingDiff, maxDeviationDiff, maxDistanceKm, k);
 * }
 * </pre>
 *
 * <p>
 * Note: This implementation assumes that the QueuedPlayer class provides
 * methods to retrieve glickoRating, glickoRD, latitude, and longitude.
 *
 * <p>
 * Attributes:
 * <ul>
 * <li>root: The root node of the KD-Tree.
 * <li>K: The number of dimensions (4 in this case).
 * <li>size: The number of nodes in the KD-Tree.
 * </ul>
 *
 * <p>
 * Methods:
 * <ul>
 * <li>{@link #MatchmakingKDTree()}: Constructor to initialize an empty KD-Tree.
 * <li>{@link #insert(QueuedPlayer)}: Inserts a player into the KD-Tree.
 * <li>{@link #findKNearest(QueuedPlayer, double, double, double, int)}: Finds
 * the k-nearest neighbors to a target player within specified constraints.
 * <li>{@link #remove(QueuedPlayer)}: Removes a player from the KD-Tree.
 * <li>{@link #isEmpty()}: Checks if the KD-Tree is empty.
 * <li>{@link #size()}: Returns the number of nodes in the KD-Tree.
 * <li>{@link #pollRootPlayer()}: Removes and returns the root player of the
 * KD-Tree.
 * <li>{@link #containsPlayer(UUID)}: Checks if a player with the specified ID
 * exists in the KD-Tree.
 * <li>{@link #removeByPlayerId(UUID)}: Removes a player with the specified ID
 * from the KD-Tree.
 * <li>{@link #getAllPlayers()}: Retrieves a list of all players in the KD-Tree.
 * </ul>
 *
 * <p>
 * Inner Classes:
 * <ul>
 * <li>{@link Node}: Represents a node in the KD-Tree, containing a player, left
 * and right children, and depth information.
 * </ul>
 *
 * <p>
 * Private Methods:
 * <ul>
 * <li>{@link #insert(Node, QueuedPlayer, int)}: Helper method to recursively
 * insert a player into the KD-Tree.
 * <li>{@link #getAxisValue(QueuedPlayer, int)}: Retrieves the value of the
 * specified axis for a player.
 * <li>{@link #findKNearest(Node, QueuedPlayer, double, double, double, int, PriorityQueue)}:
 * Helper method to recursively find the k-nearest neighbors.
 * <li>{@link #calculateDistance(QueuedPlayer, QueuedPlayer)}: Calculates the
 * Euclidean distance between two players.
 * <li>{@link #remove(Node, QueuedPlayer, int)}: Helper method to recursively
 * remove a player from the KD-Tree.
 * <li>{@link #findMin(Node, int, int)}: Finds the node with the minimum value
 * along the specified axis.
 * <li>{@link #containsPlayer(Node, UUID)}: Helper method to recursively check
 * if a player with the specified ID exists.
 * <li>{@link #removeByPlayerId(Node, UUID, int)}: Helper method to recursively
 * remove a player with the specified ID.
 * <li>{@link #getAllPlayers(Node, List)}: Helper method to recursively retrieve
 * all players in the KD-Tree.
 * </ul>
 */
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
        if (distance <= maxDistanceKm &&
                Math.abs(target.getGlickoRating() - node.player.getGlickoRating()) <= maxRatingDiff &&
                Math.abs(target.getGlickoRD() - node.player.getGlickoRD()) <= maxDeviationDiff) {
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
        if (nearestNeighbors.size() < k || Math.abs(targetValue - nodeValue) < maxDistanceKm) {
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
        if (node.player.getPlayer().getUser().getUserId().equals(playerId)) {
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
        if (node.player.getPlayer().getUser().getUserId().equals(playerId)) {
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
