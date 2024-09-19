package com.project.G1_T3.matchmaking.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.G1_T3.matchmaking.model.Match;
import com.project.G1_T3.matchmaking.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

// MatchService.java
@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public Match getCurrentMatchForUser(UUID userId) {
        PlayerProfile playerProfile = playerProfileRepository.findByUserId(userId);
        if (playerProfile == null) {
            return null;
        }

        return matchRepository.findByPlayer1IdOrPlayer2IdAndStatus(
                playerProfile.getProfileId(),
                playerProfile.getProfileId(),
                Match.MatchStatus.IN_PROGRESS);
    }
}
