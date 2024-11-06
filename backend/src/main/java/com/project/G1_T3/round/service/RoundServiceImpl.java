package com.project.G1_T3.round.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.service.StageService;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.common.model.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.lang.Math;

@Service
public class RoundServiceImpl implements RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private PlayerProfileService playerProfileService;

    // Get a round by roundId
    public Round getRoundById(UUID roundId) {
        return roundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Round not found with id: " + roundId));
    }

    // Get a list of rounds by stageId, sorted by roundNumber
    public List<Round> getRoundsByStageId(UUID stageId) {
        return roundRepository.findByStage_StageIdOrderByRoundNumber(stageId);
    }

    public void createFirstRound(UUID stageId, List<PlayerProfile> sortedPlayers) {

        if (stageId == null) {
            throw new IllegalArgumentException("Stage ID must not be null");
        }
    
        if (sortedPlayers == null || sortedPlayers.isEmpty()) {
            throw new IllegalArgumentException("Player list must not be null or empty");
        }
    
        if (sortedPlayers.size() < 2) {
            throw new IllegalArgumentException("At least two players are required to create a round");
        }
    
        // Fetch the existing stage by ID to prevent a new stage from being created
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));
    
        List<Match> matches = createMatches(sortedPlayers, stage);
        createRound(stage, 0, matches, sortedPlayers, stage.getReferees());

    }

    @Transactional
    public void endRound(UUID roundId, Map<UUID, MatchDTO> matchDTOMap) {
        if (roundId == null) {
            throw new IllegalArgumentException("Round ID must not be null");
        }
    
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("Round not found"));
    
        List<Match> roundMatches = round.getMatches();
        if (roundMatches == null || roundMatches.isEmpty()) {
            throw new IllegalStateException("No matches found for this round");
        }

        UUID curId;
        // complete matches in one shot if they are in the matchDTO map
        for (Match match : roundMatches) {
            curId = match.getId();
            if (matchDTOMap.containsKey(curId) && match.getStatus() != Status.COMPLETED) {
                matchService.completeMatch(curId, matchDTOMap.get(curId));
            }
        }
    
        List<PlayerProfile> advancingPlayers = new ArrayList<>();
    
        // Loop through the matches in the round and collect the winners
        // Throw exception if match is not complete
        for (Match match : roundMatches) {
            if (match.getStatus() != Status.COMPLETED) {
                throw new IllegalStateException("Match " + match.getMatchId() + " has not been completed.");
            }

            PlayerProfile winner = playerProfileService.findByProfileId(match.getWinnerId());
            if (winner == null) {
                throw new IllegalStateException("Winner not found for match with ID: " + match.getMatchId());
            }
            advancingPlayers.add(winner);
        }
    
        // If there are enough advancing players, create the next round
        if (advancingPlayers.size() > 1) {
            createNextRound(round.getStage(), advancingPlayers, round);
        } else if (advancingPlayers.size() == 1) {
            // Only one player left, so they are the winner of the stage
            endStage(round.getStage(), advancingPlayers.get(0));
        } else {
            throw new IllegalStateException("No players advanced to the next round");
        }
    }

    // I put this here as I can't put it in stageService since it'll cause circular dependencies
    private void endStage(Stage stage, PlayerProfile winner) {
        
        if (stage == null) {
            throw new IllegalArgumentException("Stage must not be null");
        }
    
        if (winner == null) {
            throw new IllegalArgumentException("Winner must not be null");
        }
    
        // Mark the stage as complete and declare the winner
        stage.setStatus(Status.COMPLETED);
        stage.setWinnerId(winner.getProfileId()); // Assuming the stage has a winner field
        stage.getProgressingPlayers().add(winner);
        stageRepository.save(stage);

    }

    private void createNextRound(Stage curStage, List<PlayerProfile> advancingPlayers, Round curRound) {
        if (curStage == null) {
            throw new IllegalArgumentException("Current stage must not be null");
        }
    
        if (advancingPlayers == null || advancingPlayers.size() < 2) {
            throw new IllegalArgumentException("At least two advancing players are required to create the next round");
        }
    
        if (curRound == null) {
            throw new IllegalArgumentException("Current round must not be null");
        }
    
        // Create new matches for the next round by pairing winners from the current round
        List<Match> matches = createMatches(advancingPlayers, curStage);
        createRound(curStage, curRound.getRoundNumber(), matches, advancingPlayers, curStage.getReferees());
    }

    private List<Match> createMatches(List<PlayerProfile> playerList, Stage stage) {
        if (playerList == null || playerList.size() < 2) {
            throw new IllegalArgumentException("At least two players are required to create matches");
        }
    
        if (stage == null) {
            throw new IllegalArgumentException("Stage must not be null");
        }
    
        Set<PlayerProfile> referees = stage.getReferees();
        if (referees == null || referees.isEmpty()) {
            throw new IllegalStateException("No referees available for this stage");
        }
    
        // Ensure we maintain the original bracket structure by preserving match order
        List<Match> matches = new ArrayList<>();
        List<PlayerProfile> refereeList = new ArrayList<>(referees);
    
        int totalPlayers = playerList.size();
        int lowestGreaterPowerOfTwo = Integer.highestOneBit(totalPlayers) << 1;
        int left = lowestGreaterPowerOfTwo % totalPlayers;
        int right = totalPlayers - 1;

        List<MatchDTO> matchDTOs = new ArrayList<>();

        // progress the top x players to next round if not 2^n players
        for (int i = 0; i < left; i++) {
            PlayerProfile player1 = playerList.get(i);
    
            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setPlayer1Id(player1.getProfileId());
            matchDTO.setPlayer2Id(null);
    
            // Pick a random referee if there are multiple, otherwise pick the only referee
            PlayerProfile selectedReferee;
            if (refereeList.size() == 1) {
                selectedReferee = refereeList.get(0);  // Pick the only referee
            } else {
                int randomRefereeIndex = new Random().nextInt(refereeList.size());
                selectedReferee = refereeList.get(randomRefereeIndex);  // Pick a random referee
            }
            matchDTO.setScheduledTime(LocalDateTime.now().plusDays(1));
    
            matchDTOs.add(matchDTO);
        }
    
        while (left < right) {
            PlayerProfile player1 = playerList.get(left);
            PlayerProfile player2 = playerList.get(right);
    
            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setPlayer1Id(player1.getProfileId());
            matchDTO.setPlayer2Id(player2.getProfileId());
    
            // Pick a random referee if there are multiple, otherwise pick the only referee
            
            matchDTO.setScheduledTime(LocalDateTime.now().plusDays(1));
    
            matchDTOs.add(matchDTO);

            left++;
            right--;
        }
    
        // Decoupled creation of matches
        for (MatchDTO matchDTO : matchDTOs) {
            Match match = matchService.createMatch(matchDTO);
            matches.add(match);
        }
    
        return matches;
    }    

    private void createRound(Stage stage, Integer roundNumber, List<Match> matches, List<PlayerProfile> players, Set<PlayerProfile> referees) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage must not be null");
        }

        System.out.println("round number = " + roundNumber);
    
        if (roundNumber == null || roundNumber < 0) {
            throw new IllegalArgumentException("Round number must be non-negative");
        }
    
        if (matches == null || matches.isEmpty()) {
            throw new IllegalArgumentException("Matches must not be null or empty");
        }
    
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list must not be null or empty");
        }
    
        if (referees == null || referees.isEmpty()) {
            throw new IllegalStateException("No referees available for this round");
        }
    
        Round round = new Round();
        round.setStage(stage);
        round.setRoundNumber(roundNumber + 1);
        round.setStartDate(LocalDateTime.now());
        round.setEndDate(LocalDateTime.now().plusDays(1));
        round.setStatus(Status.IN_PROGRESS);
        round.setMatches(matches);
        round.setPlayers(new HashSet<>(players));
        round.setReferees(new HashSet<>(referees));
    
        roundRepository.save(round);
    }
    

}
