package com.project.G1_T3.match.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.G1_T3.match.enums.MatchStatus;
import com.project.G1_T3.match.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId); // Find matches by tournament ID

    List<Match> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id); // Find matches involving a specific player

    List<Match> findByStatus(MatchStatus status); // Find matches by status (scheduled, ongoing, completed)
}