package com.project.G1_T3.round.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;

import java.util.List;

public interface RoundService {

    public void createFirstRound(Long stageId, List<PlayerProfile> sortedPlayers);

    public void endRound(Long roundId);

}
