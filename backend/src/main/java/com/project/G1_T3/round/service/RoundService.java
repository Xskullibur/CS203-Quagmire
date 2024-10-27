package com.project.G1_T3.round.service;

import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.stage.model.Stage;

import java.util.List;
import java.util.UUID;

public interface RoundService {

    public void createFirstRound(UUID stageId, List<PlayerProfile> sortedPlayers);

    public void endRound(UUID roundId);

}
