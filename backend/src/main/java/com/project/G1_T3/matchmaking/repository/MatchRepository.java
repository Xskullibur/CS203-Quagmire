package com.project.G1_T3.matchmaking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.G1_T3.matchmaking.model.Match;

// MatchRepository.java
public interface MatchRepository extends JpaRepository<Match, UUID> {
    Match findByPlayer1IdOrPlayer2IdAndStatus(UUID player1Id, UUID player2Id, Match.MatchStatus status);
}