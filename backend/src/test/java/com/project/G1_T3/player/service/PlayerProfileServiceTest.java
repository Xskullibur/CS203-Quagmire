package com.project.G1_T3.player.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.model.PlayerProfileDTO;
import com.project.G1_T3.player.repository.PlayerProfileRepository;
import com.project.G1_T3.security.service.AuthorizationService;
import com.project.G1_T3.security.service.SecurityService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.filestorage.service.FileStorageService;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PlayerProfileServiceTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @Mock
    private MultipartFile profileImage;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PlayerProfileService playerProfileService;

    private UUID userId;
    private PlayerProfile existingProfile;
    private PlayerProfileDTO profileUpdates;

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userId = UUID.randomUUID();

        // Set up User
        user = new User();
        user.setId(userId.toString());

        // Set up existing profile
        existingProfile = new PlayerProfile();
        existingProfile.setUser(user);
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
        when(authorizationService.authorizeUserById(any(UUID.class))).thenReturn(user);
        when(playerProfileRepository.findByUser(any(User.class))).thenReturn(existingProfile);
        when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);

        PlayerProfile updatedProfile = playerProfileService.updateProfile(userId, profileUpdates, null);

        assertNotNull(updatedProfile);
        assertEquals(profileUpdates.getFirstName(), updatedProfile.getFirstName());
        assertEquals(profileUpdates.getLastName(), updatedProfile.getLastName());
        assertEquals(profileUpdates.getBio(), updatedProfile.getBio());
        assertEquals(profileUpdates.getCountry(), updatedProfile.getCountry());
        assertEquals(profileUpdates.getDateOfBirth(), updatedProfile.getDateOfBirth());

        verify(authorizationService).authorizeUserById(any(UUID.class));
        verify(playerProfileRepository).findByUser(any(User.class));
        verify(playerProfileRepository).save(existingProfile);
    }

    @Test
    void updateProfile_UnauthorizedUser() {
        UUID differentUserId = UUID.randomUUID();
        User differentUser = new User();
        differentUser.setId(differentUserId.toString());

        doThrow(new SecurityException("User not authorized to update this profile")).when(authorizationService)
                .authorizeUserById(any(UUID.class));

        assertThrows(SecurityException.class,
                () -> playerProfileService.updateProfile(userId, profileUpdates, profileImage));

        verify(playerProfileRepository, never()).findByUserId(any());
        verify(playerProfileRepository, never()).save(any());
    }

    @Test
    void updateProfile_ProfileNotFound() {
        when(authorizationService.authorizeUserById(any(UUID.class))).thenReturn(mock(User.class));

        assertThrows(EntityNotFoundException.class,
                () -> playerProfileService.updateProfile(userId, profileUpdates, profileImage));

        verify(authorizationService).authorizeUserById(any(UUID.class));

        verify(playerProfileRepository, never()).save(any());
    }
}