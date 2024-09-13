package com.project.G1_T3.tournament;

import com.project.G1_T3.tournament.model.Tournament;
import com.project.G1_T3.tournament.repository.TournamentRepository;
import com.project.G1_T3.tournament.service.TournamentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        when(tournamentRepository.findByDateAfter(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Tournament> tournaments = tournamentService.findUpcomingTournaments(pageable);
        assertThat(tournaments).isNotNull();
        assertThat(tournaments.getContent()).isEmpty();
    }

}
