package com.project.G1_T3.achievement.service;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.repository.AchievementRepository;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.G1_T3.playerprofile.repository.*;

import java.util.List;
import java.util.Set;

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
        List<Achievement> achievementList = achievementRepository.findByCriteriaType("PARTICIPATION");

        // Get the player's tournament participation count
        int participationCount = player.getTournaments().size();

        // Get player's acheivements
        Set<Achievement> playerAchievements = player.getAchievements();

        // Check if the player has participated in * tournaments
        for (Achievement achievement : achievementList) {
            if (!playerAchievements.contains(achievement) && participationCount >= achievement.getCriteriaCount()) {
                addAchievement(player, achievement);
            } else if (playerAchievements.contains(achievement)
                    && participationCount < achievement.getCriteriaCount()) {
                removeAchievement(player, achievement);
            }
        }
    }

    public void checkRatingAchievements(PlayerProfile player) {
        // Get all achievements
        List<Achievement> achievementList = achievementRepository.findByCriteriaType("RATING");

        // Get the player's current Glicko rating
        int currentRating = Math.round(player.getGlickoRating());
        
        // Get player's acheivements
        Set<Achievement> playerAchievements = player.getAchievements();

        // Check if the player has reached a Glicko rating of *
        for (Achievement achievement : achievementList) {
            if (!playerAchievements.contains(achievement) && currentRating >= achievement.getCriteriaCount()) {
                addAchievement(player, achievement);
            } else if (playerAchievements.contains(achievement) && currentRating <= achievement.getCriteriaCount()) {
                removeAchievement(player, achievement);
            }
        }
    }

    public void addAchievement(PlayerProfile player, Achievement achievement) {
        player.getAchievements().add(achievement);

        // Save the updated player profile and achievement
        playerProfileRepository.save(player);
        achievementRepository.save(achievement);
    }

    public void removeAchievement(PlayerProfile player, Achievement achievement) {
        player.getAchievements().remove(achievement);

        // Save the updated player profile and achievement
        playerProfileRepository.save(player);
        achievementRepository.save(achievement);
    }
}
