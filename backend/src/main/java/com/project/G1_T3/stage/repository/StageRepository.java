package com.project.G1_T3.stage.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.G1_T3.stage.model.Stage;
import java.util.*;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    // Find all stages for a specific tournament
    List<Stage> findByTournamentId(UUID tournamentId);

    // Find a specific stage by its ID and tournamentId
    Optional<Stage> findByStageIdAndTournamentId(Long stageId, UUID tournamentId);
}
