package com.project.G1_T3.leaderboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.leaderboard.service.LeaderboardService;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.player.model.User;
import com.project.G1_T3.player.service.UserService;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    // Fetch the current leaderboard
    @GetMapping
    public List<LeaderboardPlayerProfile> getLeaderboard() {
        return leaderboardService.getTop10LeaderboardPlayerProfiles();
    }

    // Submit a new score for a player
    // @PostMapping
    // public ResponseEntity<String> submitScore(@RequestBody Score score) {
    // leaderboardService.submitScore(score);
    // return ResponseEntity.ok("Score submitted");
    // }

    // Get a specific player's position and info
    @GetMapping("/{username}") 
    public LeaderboardPlayerProfile getPlayerInfo(@PathVariable String username) { 
        Optional<User> u = UserService.findByUsername(username);
        long userId = u.get().getId();
     
        return leaderboardService.getPlayerInfo(userId); 
    }

    @GetMapping("user/{userId}") 
    public LeaderboardPlayerProfile getPlayerInfo(@PathVariable long userId) { 
     
        return leaderboardService.getPlayerInfo(userId); 
    }
}
