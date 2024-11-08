package com.project.G1_T3.tournament.repository;

import com.project.G1_T3.tournament.model.Tournament;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, UUID> {

    Optional<Tournament> findById(UUID id);

    // Pagination for all tournaments (already paginated)
    Page<Tournament> findAll(Pageable pageable);

    // Find tournaments by name (added pagination)
    Page<Tournament> findByName(String name, Pageable pageable);

    // Find tournaments by location (added pagination)
    Page<Tournament> findByLocation(String location, Pageable pageable);

    // Find tournaments that start after a specific date (already paginated)
    Page<Tournament> findByStartDateAfter(LocalDateTime startDate, Pageable pageable);

    // Find tournaments that end before a specific date (already paginated)
    Page<Tournament> findByEndDateBefore(LocalDateTime endDate, Pageable pageable);

    // Find tournaments where the deadline is before a specific date (already
    // paginated)
    Page<Tournament> findByDeadlineBefore(LocalDateTime deadline, Pageable pageable);

    // Find tournaments that start and end within the specified dates (added
    // pagination)
    @Query("SELECT t FROM Tournament t WHERE t.startDate >= :availableStartDate AND t.endDate <= :availableEndDate")
    Page<Tournament> findByStartAndEndDateWithinAvailability(
            @Param("availableStartDate") LocalDateTime availableStartDate,
            @Param("availableEndDate") LocalDateTime availableEndDate,
            Pageable pageable);

    // Custom query to search tournaments by name (added pagination)
    @Query("SELECT t FROM Tournament t WHERE t.name LIKE %:name%")
    Page<Tournament> searchByName(@Param("name") String name, Pageable pageable);

    // Custom query to search tournaments by keyword in description (added
    // pagination)
    @Query("SELECT t FROM Tournament t WHERE t.description LIKE %:keyword%")
    Page<Tournament> findByKeywordInDescription(@Param("keyword") String keyword, Pageable pageable);

    // Custom query to find tournaments by location (added pagination)
    @Query("SELECT t FROM Tournament t WHERE t.location = :location")
    Page<Tournament> searchByLocation(@Param("location") String location, Pageable pageable);

    Page<Tournament> findByStartDateBetweenAndStartDateGreaterThanEqual(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            LocalDateTime currentDate,
            Pageable pageable);

    Page<Tournament> findByStartDateBetweenAndStartDateLessThan(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            LocalDateTime currentDate,
            Pageable pageable);
}
