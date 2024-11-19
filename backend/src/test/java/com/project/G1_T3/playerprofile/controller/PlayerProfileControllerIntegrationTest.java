package com.project.G1_T3.playerprofile.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import com.project.G1_T3.achievement.model.Achievement;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.security.service.SecurityService;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
class PlayerProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerProfileService playerProfileService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SecurityService securityService;

    private PlayerProfile testProfile;

    private User testUser;
    private final UUID TEST_USER_ID = UUID.randomUUID();
    private final UUID TEST_PROFILE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername("testuser");

        testProfile = new PlayerProfile();
        testProfile.setProfileId(TEST_PROFILE_ID);
        testProfile.setUser(testUser);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setBio("Test bio");
        testProfile.setCountry("Test Country");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setCurrentRating(1500);

        // Mock JWT service behavior
        doNothing().when(jwtService).validateToken(anyString(), any(CustomUserDetails.class));
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());

        // Mock security service behavior
        when(securityService.getAuthenticatedUser()).thenReturn(mock(CustomUserDetails.class));
    }

    @Test
    void getUserById_ValidId_ReturnsProfile() throws Exception {
        when(playerProfileService.findByUserId(TEST_USER_ID)).thenReturn(testProfile);

        mockMvc.perform(get("/profile/{id}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testProfile.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testProfile.getLastName()))
                .andExpect(jsonPath("$.bio").value(testProfile.getBio()))
                .andExpect(jsonPath("$.country").value(testProfile.getCountry()));
    }

    @Test
    void getUserById_InvalidId_ReturnsNotFound() throws Exception {
        when(playerProfileService.findByUserId(TEST_USER_ID)).thenReturn(null);

        mockMvc.perform(get("/profile/{id}", TEST_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUsername_ValidId_ReturnsProfile() throws Exception {
        when(playerProfileService.findByUsername(testUser.getUsername())).thenReturn(testProfile);

        mockMvc.perform(get("/profile?username={username}", testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testProfile.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testProfile.getLastName()))
                .andExpect(jsonPath("$.bio").value(testProfile.getBio()))
                .andExpect(jsonPath("$.country").value(testProfile.getCountry()));
    }

    @Test
    void getUserByUsername_InvalidId_ReturnsNotFound() throws Exception {
        when(playerProfileService.findByUsername(testUser.getUsername())).thenReturn(null);

        mockMvc.perform(get("/profile?username={username}", testUser.getUsername()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByPlayerId_ValidId_ReturnsProfile() throws Exception {
        when(playerProfileService.findByProfileId(TEST_PROFILE_ID)).thenReturn(testProfile);

        mockMvc.perform(get("/profile/player/{id}", TEST_PROFILE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testProfile.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testProfile.getLastName()));
    }

    @Test
    void getPlayerRankByUserId_ValidId_ReturnsRank() throws Exception {
        when(playerProfileService.findByUserId(TEST_USER_ID)).thenReturn(testProfile);
        when(playerProfileService.getPlayerRank(TEST_PROFILE_ID)).thenReturn(1.0);

        mockMvc.perform(get("/profile/rank/{userId}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("1.0"));
    }

    @Test
    void getPlayerRankByUserId_InvalidId_ReturnsNotFound() throws Exception {
        when(playerProfileService.findByUserId(TEST_USER_ID)).thenReturn(null);

        mockMvc.perform(get("/profile/rank/{userId}", TEST_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayerAchievements_ValidUsername_ReturnsAchievements() throws Exception {
        Achievement achievement = new Achievement();
        achievement.setName("First Win");
        Set<Achievement> achievements = Set.of(achievement);
        when(playerProfileService.getPlayerAchievements("testuser")).thenReturn(achievements);

        mockMvc.perform(get("/profile/achievements").param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("First Win"));
    }

    @Test
    void getPlayerTournaments_ValidUsername_ReturnsTournaments() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setName("Spring Cup");
        Set<Tournament> tournaments = Set.of(tournament);

        when(playerProfileService.getPlayerTournaments("testuser")).thenReturn(tournaments);

        mockMvc.perform(get("/profile/tournaments").param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Spring Cup"));
    }

}
