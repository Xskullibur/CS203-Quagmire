package com.project.G1_T3.playerprofile.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.monitoring.v3.Service.Custom;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.playerprofile.model.PlayerProfile;
import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;
import com.project.G1_T3.playerprofile.service.PlayerProfileService;
import com.project.G1_T3.security.service.SecurityService;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerProfileService playerProfileService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SecurityService securityService;

    private PlayerProfile testProfile;
    private PlayerProfileDTO testProfileDTO;
    private User testUser;
    private final String TEST_USER_ID = UUID.randomUUID().toString();
    private final String TEST_PROFILE_ID = UUID.randomUUID().toString();
    private final String VALID_TOKEN = "valid.test.token";
    private final String INVALID_TOKEN = "invalid.test.token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername("testuser");

        testProfile = new PlayerProfile();
        testProfile.setProfileId(UUID.fromString(TEST_PROFILE_ID));
        testProfile.setUser(testUser);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setBio("Test bio");
        testProfile.setCountry("Test Country");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setCurrentRating(1500);

        testProfileDTO = new PlayerProfileDTO(testProfile);

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
        when(playerProfileService.getPlayerRank(TEST_PROFILE_ID)).thenReturn(1);

        mockMvc.perform(get("/profile/rank/{userId}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}