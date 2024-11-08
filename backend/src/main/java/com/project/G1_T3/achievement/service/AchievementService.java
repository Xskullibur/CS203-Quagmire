package com.project.G1_T3.achievement.service;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.repository.AchievementRepository;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.playerprofile.repository.*;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private PlayerProfileRepository playerProfileRepository;

    @Autowired 
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
        // Get all achievements
        List<Achievement> achievementList= achievementRepository.findAll();

        // Get the player's tournament participation count
        int participationCount = player.getTournaments().size();

        // Check if the player has participated in * tournaments 
        for (Achievement achievement : achievementList) {
            if (achievement.getCriteriaType().toLowerCase() == "participation" && participationCount == achievement.getCriteriaCount()) {
                updateAchievement(player, achievement);
            }
        }
    }

    public void checkRatingAchievements(PlayerProfile player) {
        // Get all achievements
        List<Achievement> achievementList= achievementRepository.findAll();

        // Get the player's current Glicko rating
        int currentRating = player.getGlickoRating();
    
        // Check if the player has reached a Glicko rating of *
        for (Achievement achievement : achievementList) {
            if (achievement.getCriteriaType().toLowerCase() == "rating" && currentRating >= achievement.getCriteriaCount()) {
                updateAchievement(player, achievement);
            }
        }
    }

    public void updateAchievement(PlayerProfile player, Achievement achievement) {
        if (!player.getAchievements().contains(achievement)) {
            // Add the achievement to the player's list of achievements
            player.getAchievements().add(achievement);
            
            // Save the updated player profile and achievement
            playerProfileRepository.save(player);
            achievementRepository.save(achievement);
        }
    }
}
