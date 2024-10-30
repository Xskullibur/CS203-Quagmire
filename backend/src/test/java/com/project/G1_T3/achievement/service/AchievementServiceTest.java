package com.project.G1_T3.achievement.service;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.achievement.repository.AchievementRepository;
import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AchievementServiceTest {

    @InjectMocks
    private AchievementService achievementService;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    private PlayerProfile player;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        // Initializes the mocks
        MockitoAnnotations.openMocks(this);

        // Initialize PlayerProfile with an empty achievement set
        player = new PlayerProfile();
        player.setProfileId(UUID.randomUUID());
        player.setAchievements(new HashSet<>());

        // Initialize an achievement
        achievement = new Achievement();
        achievement.setId(1L);
        achievement.setName("First Grip");
    }

    @Test
    void testUpdateAchievement() {
        // Mocking the behavior of achievementRepository
        when(achievementRepository.findByName("First Grip")).thenReturn(achievement);

        // Act: Call the method to update achievements
        achievementService.updateAchievement(player, "First Grip");

        // Assert: Verify that the achievement was added to the player's achievement set
        assertTrue(player.getAchievements().contains(achievement));
        verify(achievementRepository).findByName("First Grip");
        verify(playerProfileRepository).save(player); // Verify that the player was saved
    }
}