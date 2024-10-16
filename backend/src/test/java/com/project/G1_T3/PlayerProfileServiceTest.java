// package com.project.G1_T3;

// import com.project.G1_T3.player.repository.PlayerProfileRepository;
// import com.project.G1_T3.player.model.PlayerProfile;
// import com.project.G1_T3.player.service.PlayerProfileService;

// import jakarta.persistence.EntityNotFoundException;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// class PlayerProfileServiceTest {

//     @Mock
//     private PlayerProfileRepository playerProfileRepository;

//     @InjectMocks
//     private PlayerProfileService playerProfileService;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void testFindAll() {
//         List<PlayerProfile> mockProfiles = List.of(new PlayerProfile(), new PlayerProfile());
//         when(playerProfileRepository.findAll()).thenReturn(mockProfiles);

//         List<PlayerProfile> result = playerProfileService.findAll();

//         assertNotNull(result);
//         assertEquals(2, result.size());
//         verify(playerProfileRepository, times(1)).findAll();
//     }

//     @Test
//     void testFindByUserId() {
//         Long userId = 1L;
//         PlayerProfile mockProfile = new PlayerProfile();
//         when(playerProfileRepository.findByUserId(userId)).thenReturn(mockProfile);

//         PlayerProfile result = playerProfileService.findByUserId(userId);

//         assertNotNull(result);
//         verify(playerProfileRepository, times(1)).findByUserId(userId);
//     }

//     @Test
//     void testUpdateProfile() {
//         Long userId = 1L;
//         PlayerProfile existingProfile = new PlayerProfile();
//         existingProfile.setUserId(userId);
//         existingProfile.setFirstName("OldFirstName");
//         existingProfile.setLastName("OldLastName");
//         existingProfile.setBio("Old bio");
//         existingProfile.setCountry("OldCountry");
//         existingProfile.setDateOfBirth(LocalDate.of(1985, 6, 15));

//         PlayerProfile profileUpdates = new PlayerProfile();
//         profileUpdates.setFirstName("NewFirstName");
//         profileUpdates.setLastName("NewLastName");
//         profileUpdates.setBio("New bio");
//         profileUpdates.setCountry("NewCountry");
//         profileUpdates.setDateOfBirth(LocalDate.of(1990, 8, 20));

//         when(playerProfileRepository.findByUserId(userId)).thenReturn(existingProfile);
//         when(playerProfileRepository.save(any(PlayerProfile.class))).thenReturn(existingProfile);

//         PlayerProfile updatedProfile = playerProfileService.updateProfile(userId, profileUpdates);

//         assertEquals("NewFirstName", updatedProfile.getFirstName());
//         assertEquals("NewLastName", updatedProfile.getLastName());
//         assertEquals("New bio", updatedProfile.getBio());
//         assertEquals("NewCountry", updatedProfile.getCountry());
//         assertEquals(LocalDate.of(1990, 8, 20), updatedProfile.getDateOfBirth());
//         verify(playerProfileRepository).findByUserId(userId);
//         verify(playerProfileRepository).save(existingProfile);
//     }

//     @Test
//     void testUpdateProfile_NotFound() {
//         // Arrange
//         Long userId = 1L;
//         PlayerProfile profileUpdates = new PlayerProfile();

//         when(playerProfileRepository.findByUserId(userId)).thenReturn(null);

//         // Act & Assert
//         assertThrows(EntityNotFoundException.class, () -> {
//             playerProfileService.updateProfile(userId, profileUpdates);
//         });

//         verify(playerProfileRepository).findByUserId(userId);
//         verify(playerProfileRepository, never()).save(any());
//     }

//     @Test
//     void testSave() {
//         PlayerProfile profileToSave = new PlayerProfile();
//         when(playerProfileRepository.save(profileToSave)).thenReturn(profileToSave);

//         PlayerProfile result = playerProfileService.save(profileToSave);

//         assertNotNull(result);
//         verify(playerProfileRepository, times(1)).save(profileToSave);
//     }

//     // @Test
//     // void testUpdateProfilePicture() {
//     //     // Arrange
//     //     Long userId = 1L;
//     //     byte[] newPicture = new byte[]{1, 2, 3};
//     //     PlayerProfile mockProfile = new PlayerProfile();
//     //     when(playerProfileRepository.findByUserId(userId)).thenReturn(mockProfile);
//     //     when(playerProfileRepository.save(mockProfile)).thenReturn(mockProfile);

//     //     // Act
//     //     playerProfileService.updateProfilePicture(userId, newPicture);

//     //     // Assert
//     //     assertNotNull(mockProfile.getProfilePicture());
//     //     assertArrayEquals(newPicture, mockProfile.getProfilePicture());
//     //     verify(playerProfileRepository, times(1)).findByUserId(userId);
//     //     verify(playerProfileRepository, times(1)).save(mockProfile);
//     // }
// }

