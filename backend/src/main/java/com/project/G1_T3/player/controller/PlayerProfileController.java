package com.project.G1_T3.player.controller;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class PlayerProfileController {
    
    @Autowired
    private PlayerProfileService playerProfileService;

    @PostMapping("/create") // Map to URL for creating player profile
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerProfile create(@RequestBody PlayerProfile playerProfile){
        return playerProfileService.save(playerProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfile> getUserById(@PathVariable String id){
        PlayerProfile playerProfile = playerProfileService.findByUserId(id);
        
        if(playerProfile == null){
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(playerProfile);
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<PlayerProfile> getUserByPlayerId(@PathVariable String id){
        PlayerProfile playerProfile = playerProfileService.findByProfileId(id);
        
        if(playerProfile == null){
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(playerProfile);
    }

    @GetMapping("/rank/{userId}")
    public ResponseEntity<Integer> getPlayerRankByUserId(@PathVariable String userId) {
        PlayerProfile playerProfile = playerProfileService.findByUserId(userId);
        
        if (playerProfile == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Get the rank (position) of the player by their profileId
        int rank = playerProfileService.getPlayerRank(playerProfile.getProfileId().toString());
        
        return ResponseEntity.ok(rank);
    }

}
