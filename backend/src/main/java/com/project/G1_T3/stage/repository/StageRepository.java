package com.project.G1_T3.stage.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.G1_T3.stage.model.Stage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StageRepository extends JpaRepository<Stage, UUID> {

    // Find all stages for a specific tournament, in order of creation
    List<Stage> findByTournamentIdOrderByCreatedAtAsc(UUID tournamentId);

    // Find a specific stage by its ID and tournamentId
    Optional<Stage> findByStageIdAndTournamentId(UUID stageId, UUID tournamentId);
}
