package com.project.G1_T3.match.service;

import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;

public interface MatchService {

    public Match createMatch(MatchDTO matchDTO);

    public void startMatch(@PathVariable Long matchId, @RequestBody MatchDTO matchDTO);

    public void completeMatch(@PathVariable Long matchId, @RequestBody MatchDTO matchDTO);

    public Match getCurrentMatchForUser(UUID userId);

}
