package com.project.G1_T3.achievement.service;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.repository.AchievementRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.player.repository.*;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private PlayerProfileRepository playerProfileRepository;

    @Autowired // Make sure to use @Autowired if you're using constructor injection
    public AchievementService(AchievementRepository achievementRepository,
                              PlayerProfileRepository playerProfileRepository) {
        this.achievementRepository = achievementRepository;
        this.playerProfileRepository = playerProfileRepository;
    }

    // Create a new achievement
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    // Get all achievements
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    // Check achievements for a player
    public void checkAchievements(PlayerProfile player) {
        // Check participation-based achievements
        checkParticipationAchievements(player);

        // Check rating-based achievements
        checkRatingAchievements(player);
    }

    public void checkParticipationAchievements(PlayerProfile player) {
        // Check if the player has registered for their first tournament
        if (player.getTournaments().size() == 1) {
            updateAchievement(player, "First Grip");
        }
    
        // Check if the player has participated in 5 tournaments
        if (player.getTournaments().size() == 5) {
            updateAchievement(player, "Rising Star");
        }
    
        // Check if the player has participated in 50 tournaments
        if (player.getTournaments().size() == 50) {
            updateAchievement(player, "Seasoned Pro");
        }
    }

    public void checkRatingAchievements(PlayerProfile player) {
        // Get the player's current Glicko rating
        int currentRating = player.getGlickoRating();
    
        // Check if the player has reached a Glicko rating of 1200
        if (currentRating >= 1200) {
            updateAchievement(player, "Rookie Ranker");
        }
    
        // Check if the player has reached a Glicko rating of 1600
        if (currentRating >= 1600) {
            updateAchievement(player, "Top Challenger");
        }

        // Check if the player has reached a Glicko rating of 2000
        if (currentRating >= 2000) {
            updateAchievement(player, "Elite Wrestler");
        }
    }
    

    public void updateAchievement(PlayerProfile player, String achievementName) {
        // Fetch the achievement based on its name
        Achievement achievement = achievementRepository.findByName(achievementName);
        
        // Check if the achievement exists
        if (achievement != null) {
            // Check if the player already has this achievement
            if (!player.getAchievements().contains(achievement)) {
                // Add the achievement to the player's list of achievements
                player.getAchievements().add(achievement);
                
                // Save the updated player profile and achievement
                playerProfileRepository.save(player);
                achievementRepository.save(achievement);
                
            }
        } else {
            // Handle the case where the achievement is not found
            System.out.println("Achievement not found: " + achievementName);
        }
    }
    
    public Achievement findByName(String name) {
        return achievementRepository.findByName(name);
    }
}
