package com.project.G1_T3.match.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.repository.MatchRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Transactional
    public Match createMatch(MatchDTO matchDTO) {

        if (matchDTO.getPlayer1Id() == null) {
            throw new IllegalArgumentException("Player 1 ID must not be null");
        }
    
        if (matchDTO.getPlayer2Id() == null) {
            throw new IllegalArgumentException("Player 2 ID must not be null");
        }
    
        if (matchDTO.getPlayer1Id().equals(matchDTO.getPlayer2Id())) {
            throw new IllegalArgumentException("Player 1 and Player 2 cannot be the same");
        }
    
        if (matchDTO.getScheduledTime() == null) {
            throw new IllegalArgumentException("Scheduled time must not be null");
        }
    
        if (matchDTO.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }

        Match match = new Match();
        match.setPlayer1Id(matchDTO.getPlayer1Id());
        match.setPlayer2Id(matchDTO.getPlayer2Id());
        match.setRefereeId(matchDTO.getRefereeId());
        match.setScheduledTime(matchDTO.getScheduledTime());
        match.setStatus(Status.SCHEDULED);
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());

        matchRepository.save(match); // Save match to the database

        return match;
    }

    public void startMatch(Long matchId, MatchDTO matchDTO) {

        // Check for null inputs
        if (matchId == null || matchDTO == null) {
            throw new IllegalArgumentException("Match ID and match details must not be null");
        }

        // Fetch the match, or throw an exception if not found
        UUID matchUUID = UUID.fromString(matchId.toString());
        Match match = matchRepository.findById(matchUUID).orElseThrow(() -> new RuntimeException("Match not found"));

        // Ensure the match hasn't already started
        if (match.getStatus() != Status.SCHEDULED) {
            throw new IllegalStateException("Match is not scheduled");
        }
        
        // Verify the referee is the correct one
        if (match.getRefereeId() == null || !match.getRefereeId().equals(matchDTO.getRefereeId())) {
            throw new RuntimeException("Unauthorized referee");
        }

        // Start the match
        match.startMatch();
        matchRepository.save(match);
    }

    // Method for referee to complete the match and select the winner
    public void completeMatch(Long matchId, MatchDTO matchDTO) {
        // Null checks for matchId and matchDTO
        if (matchId == null || matchDTO == null) {
            throw new IllegalArgumentException("Match ID and match details must not be null");
        }
        
        // Fetch the match, or throw an exception if not found
        UUID matchUUID = UUID.fromString(matchId.toString());
        Match match = matchRepository.findById(matchUUID)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    
        // Ensure the match hasn't already been completed
        if (match.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Match is already completed");
        }
    
        // Verify the referee is authorized
        if (match.getRefereeId() == null || !match.getRefereeId().equals(matchDTO.getRefereeId())) {
            throw new RuntimeException("Unauthorized referee");
        }
    
        // Verify that the winner is one of the players in the match
        if (matchDTO.getWinnerId() == null) {
            throw new IllegalArgumentException("Winner ID must not be null");
        }
    
        if (!match.getPlayer1Id().equals(matchDTO.getWinnerId()) && !match.getPlayer2Id().equals(matchDTO.getWinnerId())) {
            throw new RuntimeException("Winner must be one of the players");
        }
    
        // Complete the match
        match.completeMatch(matchDTO.getWinnerId(), matchDTO.getScore());
        matchRepository.save(match);
    }



    
}
