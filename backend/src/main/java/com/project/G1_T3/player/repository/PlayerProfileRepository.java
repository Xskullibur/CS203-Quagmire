package com.project.G1_T3.player.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.G1_T3.player.model.PlayerProfile;

import java.util.List;
 
@Repository 
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Integer> { 

    List<PlayerProfile> findTop10ByOrderByCurrentRatingDesc();

}
