package com.project.G1_T3.player.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;
import java.util.Optional;

 
// @Repository 
// public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Integer> { 

//     List<PlayerProfile> findTop10ByOrderByCurrentRatingDesc();
//     PlayerProfile findByUserId(Long id);

// }

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Integer> {  // Changed Integer to Long

    List<PlayerProfile> findTop10ByOrderByCurrentRatingDesc(); 

    // Fetch PlayerProfile by user ID
    PlayerProfile findByUserId(Long id);
}