package com.project.G1_T3.tournament.service;

import com.project.G1_T3.common.model.Status;
import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUpcomingTournaments() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(tournamentRepository.findByStartDateAfter(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findUpcomingTournaments(pageable);
        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

    @Test
    void testCreateTournament() {
        // Arrange: Set up a sample TournamentDTO to pass to the service method
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Test Tournament");
        tournamentDTO.setLocation("New York");
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 1, 10, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2023, 12, 20, 23, 59));
        tournamentDTO.setMaxParticipants(16);
        tournamentDTO.setDescription("A test tournament");

        // Create a mock MultipartFile for the photo parameter
        MultipartFile mockPhoto = new MockMultipartFile("photo", "tournament.jpg", "image/jpeg", new byte[0]);

        // Create a Tournament entity that the repository will return when saving
        Tournament tournament = new Tournament();
        tournament.setId(UUID.randomUUID());
        tournament.setName(tournamentDTO.getName());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());
        tournament.setDeadline(tournamentDTO.getDeadline());
        tournament.setMaxParticipants(tournamentDTO.getMaxParticipants());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setStatus(Status.SCHEDULED);
        tournament.setNumStages(1);

        // Mock the save operation to return the created Tournament
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act: Call the service method with the TournamentDTO and MultipartFile
        Tournament createdTournament = tournamentService.createTournament(tournamentDTO, mockPhoto);

        // Assert: Verify the returned tournament matches the expected data
        assertThat(createdTournament).isNotNull();
        assertThat(createdTournament.getId()).isEqualTo(tournament.getId());
        assertThat(createdTournament.getName()).isEqualTo(tournamentDTO.getName());
        assertThat(createdTournament.getLocation()).isEqualTo(tournamentDTO.getLocation());
        assertThat(createdTournament.getMaxParticipants()).isEqualTo(tournamentDTO.getMaxParticipants());
        assertThat(createdTournament.getDescription()).isEqualTo(tournamentDTO.getDescription());
        assertThat(createdTournament.getStatus()).isEqualTo(Status.SCHEDULED);
        assertThat(createdTournament.getNumStages()).isEqualTo(1);
    }
}
