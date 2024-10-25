package com.project.G1_T3.player.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.security.service.SecurityService;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.filestorage.service.FileStorageService;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerProfileServiceTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private MultipartFile profileImage;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    private UUID userId;
    private PlayerProfile existingProfile;
    private PlayerProfileDTO profileUpdates;
    private CustomUserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userId = UUID.randomUUID();

        // Set up User
        user = new User();
        user.setId(userId.toString());

        // Set up UserDetails
        userDetails = new CustomUserDetails(user);

        // Set up existing profile
        existingProfile = new PlayerProfile();
        existingProfile.setUserId(user.getUserId());
        existingProfile.setFirstName("John");
        existingProfile.setLastName("Doe");
        existingProfile.setBio("Original bio");
        existingProfile.setCountry("USA");
        existingProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));

        // Set up profile updates
        PlayerProfile updatedProfile = new PlayerProfile();
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Smith");
        updatedProfile.setBio("Updated bio");
        updatedProfile.setCountry("Canada");
        updatedProfile.setDateOfBirth(LocalDate.of(1992, 2, 2));
        profileUpdates = new PlayerProfileDTO(updatedProfile);
    }

    @Test
    void updateProfile_SuccessfulUpdate() throws IOException {
        when(securityService.getAuthenticatedUser()).thenReturn(userDetails);
        when(playerProfileRepository.findByUserId(userId)).thenReturn(existingProfile);
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);

        PlayerProfile updatedProfile = playerProfileService.updateProfile(userId, profileUpdates, null);

        assertNotNull(updatedProfile);
        assertEquals(profileUpdates.getFirstName(), updatedProfile.getFirstName());
        assertEquals(profileUpdates.getLastName(), updatedProfile.getLastName());
        assertEquals(profileUpdates.getBio(), updatedProfile.getBio());
        assertEquals(profileUpdates.getCountry(), updatedProfile.getCountry());
        assertEquals(profileUpdates.getDateOfBirth(), updatedProfile.getDateOfBirth());

        verify(playerProfileRepository).findByUserId(userId);
        verify(playerProfileRepository).save(existingProfile);
    }

    @Test
    void updateProfile_UnauthorizedUser() {
        UUID differentUserId = UUID.randomUUID();
        User differentUser = new User();
        differentUser.setId(differentUserId.toString());

        CustomUserDetails differentUserDetails = new CustomUserDetails(differentUser);

        when(securityService.getAuthenticatedUser()).thenReturn(differentUserDetails);

        assertThrows(SecurityException.class,
                () -> playerProfileService.updateProfile(userId, profileUpdates, profileImage));

        verify(playerProfileRepository, never()).findByUserId(any());
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updateProfile_NullUserDetails() {
        when(securityService.getAuthenticatedUser()).thenReturn(null);

        assertThrows(SecurityException.class,
                () -> playerProfileService.updateProfile(userId, profileUpdates, profileImage));

        verify(playerProfileRepository, never()).findByUserId(any());
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updateProfile_ProfileNotFound() {
        when(securityService.getAuthenticatedUser()).thenReturn(userDetails);
        when(playerProfileRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> playerProfileService.updateProfile(userId, profileUpdates, profileImage));

        verify(playerProfileRepository).findByUserId(userId);
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updateProfile_NullUpdates() {
        when(securityService.getAuthenticatedUser()).thenReturn(userDetails);
        when(playerProfileRepository.findByUserId(userId)).thenReturn(existingProfile);

        assertThrows(NullPointerException.class,
                () -> playerProfileService.updateProfile(userId, null, profileImage));

        verify(playerProfileRepository).findByUserId(userId);
        verify(playerProfileRepository, never()).save(any());
    }
}