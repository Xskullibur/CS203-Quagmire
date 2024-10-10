package com.project.G1_T3.player.controller;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        /* Commented out for now for testing */

        // Checks if user is authenticated and authorized
        // Long loggedInUserId = Long.valueOf(userDetails.getUsername()); 
        
        // Return 403 if logged in ID != profile ID
        // if (!loggedInUserId.equals(id)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); 
        // }

        PlayerProfile updatedProfile = playerProfileService.updateProfile(id, profileUpdates);
        return ResponseEntity.ok(updatedProfile);
    }

    // For uploading profile photo
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // Define the directory and file path
        String directoryPath = "backend/images/profiles/" + id;
        String fileName = file.getOriginalFilename();  
        String filePath = directoryPath + "/" + fileName;

        try {
            // Ensure the directory exists
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();  // Create directory if not exists
            }

            // Create the destination file
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);  // Save the file locally

            // Update PlayerProfile in the database with the file path
            PlayerProfile playerProfile = playerProfileService.findByUserId(id);
            playerProfile.setProfilePicturePath(filePath);
            playerProfileService.save(playerProfile); 

            return ResponseEntity.ok("Profile picture uploaded successfully!");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
        }
    }
}
