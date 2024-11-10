package com.project.G1_T3.match.service;

import com.project.G1_T3.common.glicko.Glicko2Result;
import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.repository.MatchRepository;
import com.project.G1_T3.player.model.PlayerProfile;

import lombok.extern.slf4j.Slf4j;
import com.project.G1_T3.player.service.PlayerProfileService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerProfileService playerProfileService;

    @Override
    public Match getCurrentMatchForUser(UUID userId) {
        PlayerProfile playerProfile = playerProfileService.findByUserId(userId);
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
        try {
            PlayerProfile playerProfile = playerProfileService.findByUserId(userId);
            if (playerProfile == null) {
                throw new IllegalArgumentException("Invalid user ID");
            }

            return matchRepository.findByPlayer1IdOrPlayer2Id(
                    playerProfile.getProfileId(),
                    playerProfile.getProfileId());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Get a match by matchId
    public Match getMatchById(UUID matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));
    }

    // Get a list of matches by roundId, sorted by createdAt
    public List<Match> getMatchesByRoundId(UUID roundId) {
        return matchRepository.findByRound_RoundIdOrderByCreatedAt(roundId);
    }

    public List<Match> getCompletedMatchesByRoundId(UUID roundId) {
        return matchRepository.findByRoundIdAndStatusOrderByCreatedAt(roundId, Status.COMPLETED);
    }

    @Transactional
    public Match createMatch(MatchDTO matchDTO) {

        if (matchDTO.getPlayer1Id() == null) {
            throw new IllegalArgumentException("Player 1 ID must not be null");
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
        match.setGameType(Match.GameType.SOLO);
        match.setPlayer1Id(matchDTO.getPlayer1Id());
        match.setPlayer2Id(matchDTO.getPlayer2Id());
        match.setStatus(Status.SCHEDULED);
        match.setMeetingLatitude(matchDTO.getMeetingLatitude());
        match.setMeetingLongitude(matchDTO.getMeetingLongitude());
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());

        if (matchDTO.getPlayer2Id() == null) {
            match.setWinnerId(matchDTO.getPlayer1Id());
            match.setScore("auto-progress");
            match.setStatus(Status.COMPLETED);
        }

        match = matchRepository.save(match);

        return match;
    }

    public void startMatch(UUID matchId, MatchDTO matchDTO) {

        // Check for null inputs
        if (matchId == null || matchDTO == null) {
            throw new IllegalArgumentException("Match ID and match details must not be null");
        }

        // Fetch the match, or throw an exception if not found
        UUID matchUUID = UUID.fromString(matchId.toString());
        Match match = matchRepository.findById(matchUUID).orElseThrow(() -> new RuntimeException("Match not found"));

        // Ensure the match hasn't already started
        if (match.getStatus() != Status.SCHEDULED) {
            throw new IllegalStateException("Match is not scheduled. Match is " + match.getStatus());
        }
       
        // Start the match
        match.startMatch();
        matchRepository.save(match);
    }

    // Method for referee to complete the match and select the winner
    public void completeMatch(UUID matchId, MatchDTO matchDTO) {
        System.out.println(matchDTO.getPlayer1Id());

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
    
        // Verify that the winner is one of the players in the match
        if (matchDTO.getWinnerId() == null) {
            throw new IllegalArgumentException("Winner ID must not be null");
        }
    
        if (!match.getPlayer1Id().equals(matchDTO.getWinnerId()) && !match.getPlayer2Id().equals(matchDTO.getWinnerId())) {
            throw new RuntimeException("Winner must be one of the players. The two players are " + match.getPlayer1Id() + 
            " and " + match.getPlayer2Id() + ", you gave me " + matchDTO.getWinnerId());
        }
    
        // Complete the match
        match.completeMatch(matchDTO.getWinnerId(), matchDTO.getScore());
        matchRepository.save(match);

        //update the player rankings
        updatePlayerRatingsAfterMatch(match);

    }

    private void updatePlayerRatingsAfterMatch(Match match) {
        UUID player1Id = match.getPlayer1Id();
        UUID player2Id = match.getPlayer2Id();
        UUID winnerId = match.getWinnerId();

        // Retrieve player profiles (from cache or database)
        PlayerProfile player1 = playerProfileService.findByProfileId(player1Id.toString());
        PlayerProfile player2 = playerProfileService.findByProfileId(player2Id.toString());

        // Determine match outcome
        double scorePlayer1;
        double scorePlayer2;

        if (winnerId.equals(player1Id)) {
            scorePlayer1 = 1.0; // Player 1 won
            scorePlayer2 = 0.0; // Player 2 lost
        } else if (winnerId.equals(player2Id)) {
            scorePlayer1 = 0.0; // Player 1 lost
            scorePlayer2 = 1.0; // Player 2 won
        } else {
            scorePlayer1 = 0.5; // Draw
            scorePlayer2 = 0.5;
        }

        // Create Glicko2Result for each player
        Glicko2Result resultForPlayer1 = new Glicko2Result(
                player2.getGlickoRating(),
                player2.getRatingDeviation(),
                scorePlayer1);

        Glicko2Result resultForPlayer2 = new Glicko2Result(
                player1.getGlickoRating(),
                player1.getRatingDeviation(),
                scorePlayer2);

        // Update ratings
        List<Glicko2Result> resultsForPlayer1 = new ArrayList<>();
        resultsForPlayer1.add(resultForPlayer1);
        player1.updateRating(resultsForPlayer1);

        List<Glicko2Result> resultsForPlayer2 = new ArrayList<>();
        resultsForPlayer2.add(resultForPlayer2);
        player2.updateRating(resultsForPlayer2);

        // Save updated profiles (also evicts cache entries)
        playerProfileService.updatePlayerRating(player1);
        playerProfileService.updatePlayerRating(player2);
    }

}
