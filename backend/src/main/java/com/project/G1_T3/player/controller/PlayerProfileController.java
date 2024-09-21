package com.project.G1_T3.player.controller;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<PlayerProfile> getUserById(@PathVariable Long id){
        PlayerProfile playerProfile = playerProfileService.findByUserId(id);
        return ResponseEntity.ok(playerProfile);
    }

    // For editing profile
    @PutMapping("/{id}/edit")
    public ResponseEntity<PlayerProfile> updateProfile(
        @PathVariable Long id,
        @RequestBody PlayerProfile profileUpdates,
        @AuthenticationPrincipal UserDetails userDetails) {

        // Checks if user is authenticated and authorized
        Long loggedInUserId = Long.valueOf(userDetails.getUsername()); 

        // Return 403 if logged in ID != profile ID
        if (!loggedInUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); 
        }

        // Proceed with updating the profile if the user is authorized
        PlayerProfile updatedProfile = playerProfileService.updateProfile(id, profileUpdates);
        return ResponseEntity.ok(updatedProfile);
    }

}
