package com.project.G1_T3.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.G1_T3.match.model.MatchResult;
import java.util.*;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findByWinnerId(Long winnerId); // Find results where a specific player is the winner

    List<MatchResult> findByLoserId(Long loserId); // Find results where a specific player is the loser

    List<MatchResult> findByMatchId(Long matchId); // Find results by match ID
}
