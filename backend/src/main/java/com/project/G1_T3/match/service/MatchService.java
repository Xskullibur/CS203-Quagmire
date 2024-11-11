package com.project.G1_T3.match.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;
import java.util.List;

public interface MatchService {

    public Match getMatchById(UUID matchId);

    public List<Match> getMatchesByRoundId(UUID roundId);

    public List<Match> getCompletedMatchesByRoundId(UUID roundId);

    public Match createMatch(MatchDTO matchDTO);

    public void startMatch(@PathVariable UUID matchId, @RequestBody MatchDTO matchDTO);

    public void completeMatch(@PathVariable UUID matchId, @RequestBody MatchDTO matchDTO);

    public Match getCurrentMatchForUserById(UUID userId);

    public Match forfeitMatch(UUID matchId, UUID forfeitedById);

    public Match completeMatch(UUID matchId, UUID winnerId, String score);
}
