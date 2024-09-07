package com.project.G1_T3.tournament.repository;

import com.project.G1_T3.tournament.model.Tournament;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
    List<Tournament> findByName(String name);

    List<Tournament> findByLocation(String location);

    List<Tournament> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Tournament> findByDateAfter(LocalDateTime date);

    @Query("SELECT t FROM Tournament t WHERE t.name LIKE %:name%")
    List<Tournament> searchByName(@Param("name") String name);

    @Query("SELECT t FROM Tournament t WHERE t.description LIKE %:keyword%")
    List<Tournament> findByKeywordInDescription(@Param("keyword") String keyword);

    @Query("SELECT t FROM Tournament t WHERE t.location = :city")
    List<Tournament> findByCity(@Param("city") String city);

    Page<Tournament> findAll(Pageable pageable);
}
