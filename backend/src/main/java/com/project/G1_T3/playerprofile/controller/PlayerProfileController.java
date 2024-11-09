package com.project.G1_T3.playerprofile.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.leaderboard.model.LeaderboardPlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.tournament.model.Tournament;

@RestController
@RequestMapping("/profile")
public class PlayerProfileController {

    @Autowired
    private PlayerProfileService playerProfileService;

    @GetMapping()
    public ResponseEntity<PlayerProfileDTO> getUserByUsername(@RequestParam String username) {
        PlayerProfile playerProfile = playerProfileService.findByUsername(username);

        if (playerProfile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PlayerProfileDTO(playerProfile));
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
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException {

        PlayerProfile updatedProfile = playerProfileService.updateProfile(UUID.fromString(id),
                profileUpdates, profileImage);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PlayerProfile> createProfile(
            @RequestPart("id") String id,
            @RequestPart("profileUpdates") PlayerProfileDTO profileUpdates,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException {

        PlayerProfile newProfile = playerProfileService.createProfile(UUID.fromString(id),
            profileUpdates, profileImage);

        return ResponseEntity.created(URI.create("/profile/" + newProfile.getProfileId())).build();
    }

    @GetMapping("/achievements")
    public Set<Achievement> getPlayerAchievements(@RequestParam String username) {
        Set<Achievement> achievements = playerProfileService.getPlayerAchievements(username);

        return achievements;
    }

    @GetMapping("/tournaments")
    public Set<Tournament> getPlayerTournaments(@RequestParam String username) {
        Set<Tournament> tournaments = playerProfileService.getPlayerTournaments(username);

        return tournaments;
    }
}
