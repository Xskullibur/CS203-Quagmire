package com.project.G1_T3.round.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.G1_T3.round.model.Round;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoundRepository extends JpaRepository<Round, UUID> {

    // // Custom query to find rounds by stage ID
    // List<Round> findByStageStageId(UUID stageId);

    // @Query("SELECT sr FROM Round sr WHERE sr.stage.id = :stageId AND sr.roundNumber > :roundNumber ORDER BY sr.roundNumber ASC")
    // Optional<Round> findNextRoundByStageAndRoundNumber(@Param("stageId") UUID stageId, @Param("roundNumber") Integer roundNumber);

    // Find rounds by stageId and order them by roundNumber
    List<Round> findByStage_StageIdOrderByRoundNumber(UUID stageId);

}
