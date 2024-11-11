package com.project.G1_T3.tournament.service;

import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.model.TournamentDTO;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.common.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional // Ensures a rollback after each teslows rollback after each test
public class TournamentIntegrationTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentService tournamentService;

    private TournamentDTO tournamentDTO;

    @Bean
    public AuthenticationManager authenticationManager() {
        // Return a mock or default AuthenticationManager for testing
        return authentication -> null;
    }

    @BeforeEach
    void setUp() {
        // Initialize TournamentDTO for testing
        tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("Summer Championship");
        tournamentDTO.setDescription("A high-level tournament held every summer.");
        tournamentDTO.setLocation("New York, USA");
        tournamentDTO.setMaxParticipants(64);
        tournamentDTO.setStartDate(LocalDateTime.of(2024, 6, 1, 10, 0));
        tournamentDTO.setEndDate(LocalDateTime.of(2024, 7, 1, 18, 0));
        tournamentDTO.setDeadline(LocalDateTime.of(2024, 5, 1, 23, 59));
        tournamentDTO.setStatus(Status.SCHEDULED);
    }

    @Test
    @Rollback // Ensures the database is rolled back after the test
    void shouldSaveAndRetrieveTournament() {
        // Act: Create tournament from TournamentDTO using the service



        Tournament savedTournament = tournamentService.createTournament(tournamentDTO, null);

        // Assert: Verify the saved tournament exists in the repository
        Optional<Tournament> retrievedTournament = tournamentRepository.findById(savedTournament.getId());
        assertTrue(retrievedTournament.isPresent(), "Tournament should be saved and retrievable");

        // Assert: Verify that retrieved tournament matches expected values
        Tournament tournament = retrievedTournament.get();
        assertThat(tournament.getName()).isEqualTo(tournamentDTO.getName());
        assertThat(tournament.getDescription()).isEqualTo(tournamentDTO.getDescription());
        assertThat(tournament.getLocation()).isEqualTo(tournamentDTO.getLocation());
        assertThat(tournament.getMaxParticipants()).isEqualTo(tournamentDTO.getMaxParticipants());
        assertThat(tournament.getStatus()).isEqualTo(Status.SCHEDULED);
        assertThat(tournament.getStartDate()).isEqualTo(tournamentDTO.getStartDate());
        assertThat(tournament.getEndDate()).isEqualTo(tournamentDTO.getEndDate());
        assertThat(tournament.getDeadline()).isEqualTo(tournamentDTO.getDeadline());
    }
}
