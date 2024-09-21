package com.project.G1_T3.tournament;

import com.project.G1_T3.tournament.repository.*;
import com.project.G1_T3.tournament.model.Tournament;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TournamentRepositoryTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    public void whenFindById_thenReturnTournament() {
        // given
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setLocation("Test Location");
        tournament.setStartDate(LocalDateTime.now().plusDays(1));
        tournament.setEndDate(LocalDateTime.now().plusDays(2));
        tournament.setDeadline(LocalDateTime.now());
        tournament.setDescription("Test Description");

        tournament = tournamentRepository.save(tournament);

        // when
        Optional<Tournament> found = tournamentRepository.findById(tournament.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(tournament.getName());
    }

    @Test
    public void whenFindAll_thenReturnTournamentList() {
        // given
        int initialCount = tournamentRepository.findAll().size();

        Tournament newTournament = new Tournament();
        newTournament.setName("New Test Tournament");
        newTournament.setLocation("New Test Location");
        newTournament.setStartDate(LocalDateTime.now().plusDays(1));
        newTournament.setEndDate(LocalDateTime.now().plusDays(2));
        newTournament.setDeadline(LocalDateTime.now());
        newTournament.setDescription("New Test Description");

        tournamentRepository.save(newTournament);

        // when
        List<Tournament> tournaments = tournamentRepository.findAll();

        // then
        assertThat(tournaments).hasSize(initialCount + 1);
        assertThat(tournaments).extracting(Tournament::getName).contains("New Test Tournament");
    }

    @Test
    public void whenSave_thenPersistTournament() {
        // given
        Tournament tournament = new Tournament();
        tournament.setName("New Tournament");
        tournament.setLocation("New Location");
        tournament.setStartDate(LocalDateTime.now().plusDays(1));
        tournament.setEndDate(LocalDateTime.now().plusDays(2));
        tournament.setDeadline(LocalDateTime.now());
        tournament.setDescription("New Description");

        // when
        Tournament saved = tournamentRepository.save(tournament);

        // then
        assertThat(saved).hasFieldOrPropertyWithValue("name", "New Tournament");
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    public void whenDelete_thenRemoveTournament() {
        // given
        Tournament tournament = new Tournament();
        tournament.setName("Tournament to Delete");
        tournament.setLocation("Location");
        tournament.setStartDate(LocalDateTime.now().plusDays(1));
        tournament.setEndDate(LocalDateTime.now().plusDays(2));
        tournament.setDeadline(LocalDateTime.now());
        tournament.setDescription("Description");

        tournament = tournamentRepository.save(tournament);

        // when
        tournamentRepository.delete(tournament);

        // then
        Optional<Tournament> deleted = tournamentRepository.findById(tournament.getId());
        assertThat(deleted).isEmpty();
    }
}
