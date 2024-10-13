package com.project.G1_T3;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.service.PlayerProfileService;
import com.project.G1_T3.player.controller.PlayerProfileController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PlayerProfileControllerTest {

    @Mock
    private PlayerProfileService playerProfileService;

    @InjectMocks
    private PlayerProfileController playerProfileController;

    @Mock
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProfile() {
        PlayerProfile profileToCreate = new PlayerProfile();
        when(playerProfileService.save(profileToCreate)).thenReturn(profileToCreate);

        PlayerProfile result = playerProfileController.create(profileToCreate);

        assertNotNull(result);
        assertEquals(profileToCreate, result);
        verify(playerProfileService, times(1)).save(profileToCreate);
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        PlayerProfile mockProfile = new PlayerProfile();
        when(playerProfileService.findByUserId(userId)).thenReturn(mockProfile);

        ResponseEntity<PlayerProfile> response = playerProfileController.getUserById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProfile, response.getBody());
        verify(playerProfileService, times(1)).findByUserId(userId);
    }

    @Test
    void testUpdateProfileAuthorized() {
        Long userId = 1L;
        PlayerProfile updatedProfile = new PlayerProfile();
        when(mockUserDetails.getUsername()).thenReturn(String.valueOf(userId));
        when(playerProfileService.updateProfile(userId, updatedProfile)).thenReturn(updatedProfile);
        
        ResponseEntity<PlayerProfile> response = playerProfileController.updateProfile(userId, updatedProfile, mockUserDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProfile, response.getBody());
        verify(playerProfileService).updateProfile(anyLong(), any(PlayerProfile.class));
    }

    @Test
    void testUpdateProfileUnauthorized() {
        Long userId = 1L;
        PlayerProfile updatedProfile = new PlayerProfile();

        // Mock a different userId to simulate unauthorized access
        // Pretend this is a different user
        when(mockUserDetails.getUsername()).thenReturn("2"); 

        ResponseEntity<PlayerProfile> response = playerProfileController.updateProfile(userId, updatedProfile, mockUserDetails);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(playerProfileService, never()).updateProfile(anyLong(), any(PlayerProfile.class));
    }

    // @Test
    // void testUploadProfilePicture() {
    //     // Arrange
    //     Long userId = 1L;
    //     byte[] picture = new byte[]{1, 2, 3};
    //     PlayerProfile mockProfile = new PlayerProfile();
    //     when(playerProfileService.updateProfilePicture(userId, picture)).thenReturn(mockProfile);

    //     // Act
    //     ResponseEntity<String> response = playerProfileController.uploadProfilePicture(userId, new MockMultipartFile("file", picture));

    //     // Assert
    //     assertNotNull(response);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     verify(playerProfileService, times(1)).updateProfilePicture(userId, picture);
    // }
}
