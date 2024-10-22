package com.project.G1_T3.player.controller;

import com.project.G1_T3.player.model.PlayerProfileRequest;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<PlayerProfile> getUserById(@PathVariable String id) {
        PlayerProfile playerProfile = playerProfileService.findByUserId(id);

        if (playerProfile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(playerProfile);
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
    @PutMapping("/edit")
    public ResponseEntity<PlayerProfile> updateProfile(@RequestBody PlayerProfileRequest playerProfileRequest) {

        PlayerProfile updatedProfile = playerProfileService.updateProfile(UUID.fromString(playerProfileRequest.getId()),
                playerProfileRequest.getProfileUpdates());

        return ResponseEntity.ok(updatedProfile);
    }

    // Commented out for unit testing
    // // For uploading profile photo
    // @PostMapping("/{id}/upload")
    // public ResponseEntity<String> uploadProfilePicture(@PathVariable Long id,
    // String filePath) {
    // // Define the directory and file path
    // String directoryPath = "backend/images/profiles/" + id;
    // // String fileName = file.getOriginalFilename();
    // // String filePath = directoryPath + "/" + fileName;

    // try {
    // // Ensure the directory exists
    // File directory = new File(directoryPath);
    // if (!directory.exists()) {
    // directory.mkdirs(); // Create directory if not exists
    // }

    // // Create the destination file
    // File originalFile = new File(filePath);
    // originalFile.transferTo(directory); // Save the file locally

    // // Update PlayerProfile in the database with the file path
    // PlayerProfile playerProfile = playerProfileService.findByUserId(id);
    // playerProfile.setProfilePicturePath(filePath);
    // playerProfileService.save(playerProfile);

    // return ResponseEntity.ok("Profile picture uploaded successfully!");

    // } catch (IOException e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed
    // to upload profile picture.");
    // }
    // }
}
