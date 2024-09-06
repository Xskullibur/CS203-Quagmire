package com.project.G1_T3.tournament.repository;

import com.project.G1_T3.tournament.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // Custom query methods can be added here if needed
}
