package com.project.G1_T3.round.service;

import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.round.model.Round;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RoundService {

    public void createFirstRound(UUID stageId, List<PlayerProfile> sortedPlayers);

    public void endRound(UUID roundId, Map<UUID, MatchDTO> matchDTOMap);

    public Round getRoundById(UUID roundId);

    public List<Round> getRoundsByStageId(UUID stageId);

}
