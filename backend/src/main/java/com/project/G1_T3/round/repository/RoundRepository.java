package com.project.G1_T3.round.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.G1_T3.round.model.Round;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    // // Custom query to find rounds by stage ID
    // List<Round> findByStageStageId(Long stageId);

    // @Query("SELECT sr FROM Round sr WHERE sr.stage.id = :stageId AND sr.roundNumber > :roundNumber ORDER BY sr.roundNumber ASC")
    // Optional<Round> findNextRoundByStageAndRoundNumber(@Param("stageId") Long stageId, @Param("roundNumber") Integer roundNumber);
}
