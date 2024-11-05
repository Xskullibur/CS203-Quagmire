package com.project.G1_T3.match.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;

// MatchRepository.java
public interface MatchRepository extends JpaRepository<Match, UUID> {
    Match findByPlayer1IdOrPlayer2IdAndStatus(UUID player1Id, UUID player2Id, Status status);
    Match findByPlayer1IdOrPlayer2Id(UUID player1Id, UUID player2Id);

    // Find matches by roundId and order them by createdAt
    List<Match> findByRound_RoundIdOrderByCreatedAt(UUID roundId);
    
    @Query("SELECT m FROM Match m WHERE m.round.roundId = :roundId AND m.status = :status ORDER BY m.createdAt")
    List<Match> findByRoundIdAndStatusOrderByCreatedAt( UUID roundId,Status status);
}
