package com.project.G1_T3.player.controller;

import com.project.G1_T3.player.model.PlayerProfileRequest;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;
import com.project.G1_T3.player.service.PlayerProfileService;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
public class PlayerProfileController {

    @Autowired
    private PlayerProfileService playerProfileService;

    @PostMapping("/create") // Map to URL for creating player profile
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerProfile create(@RequestBody PlayerProfile playerProfile) {
        return playerProfileService.save(playerProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfileDTO> getUserById(@PathVariable String id) {
        PlayerProfile playerProfile = playerProfileService.findByUserId(id);

        if (playerProfile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PlayerProfileDTO(playerProfile));
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<PlayerProfile> getUserByPlayerId(@PathVariable String id) {
        PlayerProfile playerProfile = playerProfileService.findByProfileId(id);

        if (playerProfile == null) {
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

    // For editing profile
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerProfile> updateProfile(
            @RequestPart("id") String id,
            @RequestPart("profileUpdates") PlayerProfileDTO profileUpdates,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        PlayerProfile updatedProfile = playerProfileService.updateProfile(UUID.fromString(id), profileUpdates, profileImage);
        return ResponseEntity.ok(updatedProfile);
    }

}
