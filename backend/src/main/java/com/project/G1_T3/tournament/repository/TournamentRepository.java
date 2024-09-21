package com.project.G1_T3.tournament.repository;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.tournament.model.Tournament;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
    Optional<Tournament> findById(long id);

    Tournament update(Long id, Tournament newTournament);

    // Find tournaments by name
    List<Tournament> findByName(String name);

    // Find tournaments by location
    List<Tournament> findByLocation(String location);

    // Find tournaments by a range of start dates
    List<Tournament> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find tournaments that start after a specific date
    Page<Tournament> findByStartDateAfter(LocalDateTime startDate, Pageable pageable);

    // Find tournaments that end before a specific date
    Page<Tournament> findByEndDateBefore(LocalDateTime endDate, Pageable pageable);

    // Find tournaments where the deadline is before a specific date
    Page<Tournament> findByDeadlineBefore(LocalDateTime deadline, Pageable pageable);

    // Custom query to search tournaments by name
    @Query("SELECT t FROM Tournament t WHERE t.name LIKE %:name%")
    List<Tournament> searchByName(@Param("name") String name);

    // Custom query to search tournaments by keyword in description
    @Query("SELECT t FROM Tournament t WHERE t.description LIKE %:keyword%")
    List<Tournament> findByKeywordInDescription(@Param("keyword") String keyword);

    // Custom query to find tournaments by city (location)
    @Query("SELECT t FROM Tournament t WHERE t.location = :city")
    List<Tournament> findByCity(@Param("city") String city);

    Set<PlayerProfile> getPlayers(Long tournamentID);

    // Pagination for all tournaments
    Page<Tournament> findAll(Pageable pageable);
}
