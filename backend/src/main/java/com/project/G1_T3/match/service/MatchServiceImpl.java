package com.project.G1_T3.match.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    @Override
    public Match getCurrentMatchForUser(UUID userId) {
        PlayerProfile playerProfile = playerProfileRepository.findByUserId(userId);
        if (playerProfile == null) {
            return null;
        }

        return matchRepository.findByPlayer1IdOrPlayer2IdAndStatus(
                playerProfile.getProfileId(),
                playerProfile.getProfileId(),
                Status.IN_PROGRESS);
    }

    @Override
    public Match getCurrentMatchForUserById(UUID userId) {
        PlayerProfile playerProfile = playerProfileRepository.findByUserId(userId);
        if (playerProfile == null) {
            return null;
        }

        return matchRepository.findByPlayer1IdOrPlayer2Id(
                playerProfile.getProfileId(),
                playerProfile.getProfileId());
    }

    @Transactional
    public Match createMatch(MatchDTO matchDTO) {
        Match match = new Match();
        match.setGameType(Match.GameType.SOLO);
        match.setPlayer1Id(matchDTO.getPlayer1Id());
        match.setPlayer2Id(matchDTO.getPlayer2Id());
        match.setStatus(Status.SCHEDULED);
        match.setMeetingLatitude(matchDTO.getMeetingLatitude());
        match.setMeetingLongitude(matchDTO.getMeetingLongitude());
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());

        match = matchRepository.save(match);

        return match;
    }

    public void startMatch(Long matchId, MatchDTO matchDTO) {
        UUID matchUUID = UUID.fromString(matchId.toString());
        Match match = matchRepository.findById(matchUUID).orElseThrow(() -> new RuntimeException("Match not found"));

        // Verify the referee is the correct one
        if (!match.getRefereeId().equals(matchDTO.getRefereeId())) {
            throw new RuntimeException("Unauthorized referee");
        }

        // Start the match
        match.startMatch();
        matchRepository.save(match);
    }

    // Method for referee to complete the match and select the winner
    public void completeMatch(Long matchId, MatchDTO matchDTO) {
        UUID matchUUID = UUID.fromString(matchId.toString());
        Match match = matchRepository.findById(matchUUID).orElseThrow(() -> new RuntimeException("Match not found"));

        // Verify the referee is the correct one
        if (!match.getRefereeId().equals(matchDTO.getRefereeId())) {
            throw new RuntimeException("Unauthorized referee");
        }

        // Verify that the players correct
        if (!match.getPlayer1Id().equals(matchDTO.getWinnerId())
                && !match.getPlayer2Id().equals(matchDTO.getWinnerId())) {
            throw new RuntimeException("Unauthorized referee");
        }

        // Complete the match
        match.completeMatch(matchDTO.getWinnerId(), matchDTO.getScore());
        matchRepository.save(match);
    }
}
