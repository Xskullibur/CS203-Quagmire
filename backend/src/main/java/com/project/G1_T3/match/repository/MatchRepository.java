package com.project.G1_T3.match.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;

// MatchRepository.java
public interface MatchRepository extends JpaRepository<Match, UUID> {
    @Query("SELECT m FROM Match m WHERE (m.player1Id = :playerId OR m.player2Id = :playerId) AND m.status = :status")
    List<Match> findMatchesByPlayerIdAndStatus(@Param("playerId") UUID playerId, @Param("status") Status status);

    Match findByPlayer1IdOrPlayer2Id(UUID player1Id, UUID player2Id);
}
