package com.project.G1_T3.round.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.round.model.Round;
import com.project.G1_T3.round.repository.RoundRepository;
import com.project.G1_T3.stage.model.Stage;
import com.project.G1_T3.stage.repository.StageRepository;
import com.project.G1_T3.match.model.Match;
import com.project.G1_T3.match.model.MatchDTO;
import com.project.G1_T3.match.service.MatchService;
import com.project.G1_T3.common.model.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@Service
public class RoundServiceImpl implements RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public void createFirstRound(Long stageId, List<PlayerProfile> sortedPlayers) {

        // Fetch the existing stage by ID to prevent a new stage from being created
        Stage stage = stageRepository.findById(stageId)
                        .orElseThrow(() -> new RuntimeException("Stage not found"));

        List<Match> matches = new ArrayList<>();

        Set<PlayerProfile> referees = stage.getReferees();
        List<PlayerProfile> refereeList = new ArrayList<>(referees);

        int totalPlayers = sortedPlayers.size();
        for (int i = 0; i < totalPlayers / 2; i++) {
            PlayerProfile player1 = sortedPlayers.get(i);
            PlayerProfile player2 = sortedPlayers.get(totalPlayers - 1 - i);

            System.out.println("adding players" + player1.getCurrentRating() + " " + player2.getCurrentRating());

            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setPlayer1Id(player1.getProfileId());
            matchDTO.setPlayer2Id(player2.getProfileId());
            // matchDTO.setRound(round);
            // matchDTO.setStatus(Status.SCHEDULED);  // Default status for a new match

            // Pick a random referee if there are multiple, otherwise pick the only referee
            PlayerProfile selectedReferee;
            if (refereeList.size() == 1) {
                selectedReferee = refereeList.get(0);  // Pick the only referee
            } else {
                int randomRefereeIndex = new Random().nextInt(refereeList.size());
                selectedReferee = refereeList.get(randomRefereeIndex);  // Pick a random referee
            }

            matchDTO.setRefereeId(selectedReferee.getProfileId());
            matchDTO.setScheduledTime(LocalDateTime.now().plusDays(1));
            
            Match match = matchService.createMatch(matchDTO);

            matches.add(match);
        }

        Round round = new Round();
        round.setStage(stage);
        round.setRoundNumber(1);
        round.setStartDate(LocalDateTime.now());
        round.setEndDate(LocalDateTime.now().plusDays(1));
        round.setStatus(Status.SCHEDULED);
        round.setMatches(matches);
        System.out.println("insert length of roundMatches = " + round.getMatches().size());
        round.setPlayers(new HashSet<>(stage.getPlayers()));
        round.setReferees(new HashSet<>(referees));

        roundRepository.save(round);
    }

    public void endRound(Long roundId) {
        Round round = roundRepository.findById(roundId)
                        .orElseThrow(() -> new RuntimeException("Round not found"));
    
        List<PlayerProfile> advancingPlayers = new ArrayList<>();

        System.out.println("length of roundMatches = " + round.getMatches().size());
    
        // Loop through the matches in the round and collect the winners
        for (Match match : round.getMatches()) {
            System.out.println("Winner: " + match.getWinnerId());
            PlayerProfile winner = playerProfileRepository.findByProfileId(match.getWinnerId()); // Assume this is set after the match ends
            System.out.println("Winner profile: " + winner.getProfileId());
            advancingPlayers.add(winner);
        }
    
        // If there are enough advancing players, create the next round
        if (advancingPlayers.size() > 1) {
            createNextRound(round.getStage(), advancingPlayers);
        } else {
            // Only one player left, so they are the winner of the stage
            endStage(round.getStage(), advancingPlayers.get(0));
        }
    }

    private void createNextRound(Stage curStage, List<PlayerProfile> advancingPlayers) {

        // Ensure we maintain the original bracket structure by preserving match order
        List<Match> matches = new ArrayList<>();

        Set<PlayerProfile> referees = curStage.getReferees();
        List<PlayerProfile> refereeList = new ArrayList<>(referees);

        // Create new matches for the next round by pairing winners from the current round
        for (int i = 0; i < advancingPlayers.size() / 2; i++) {
            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setPlayer1Id(advancingPlayers.get(i).getProfileId());
            matchDTO.setPlayer2Id(advancingPlayers.get(advancingPlayers.size() - 1 - i).getProfileId());
            // match.setRound(nextRound);
            // match.setStatus(Status.SCHEDULED);

            // Pick a random referee if there are multiple, otherwise pick the only referee
            PlayerProfile selectedReferee;
            if (refereeList.size() == 1) {
                selectedReferee = refereeList.get(0);  // Pick the only referee
            } else {
                int randomRefereeIndex = new Random().nextInt(refereeList.size());
                selectedReferee = refereeList.get(randomRefereeIndex);  // Pick a random referee
            }
            matchDTO.setRefereeId(selectedReferee.getProfileId());
            matchDTO.setScheduledTime(LocalDateTime.now().plusDays(1));
        
            Match match = matchService.createMatch(matchDTO);

            matches.add(match);
        }

        Round nextRound = new Round();
        nextRound.setStage(curStage);
        nextRound.setRoundNumber(1);
        nextRound.setStartDate(LocalDateTime.now());
        nextRound.setEndDate(LocalDateTime.now().plusDays(1));
        nextRound.setStatus(Status.SCHEDULED);
        nextRound.setMatches(matches);
        nextRound.setPlayers(new HashSet<>(advancingPlayers));
        nextRound.setReferees(new HashSet<>(referees));
        System.out.println("insert length of roundMatches = " + nextRound.getMatches().size());
        roundRepository.save(nextRound);

    }
    
    private void endStage(Stage stage, PlayerProfile winner) {
        // Mark the stage as complete and declare the winner
        stage.setStatus(Status.COMPLETED);
        stage.setWinnerId(winner.getProfileId()); // Assuming the stage has a winner field
        stageRepository.save(stage);
    }

}
