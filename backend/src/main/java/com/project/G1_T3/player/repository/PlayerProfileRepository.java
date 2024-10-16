package com.project.G1_T3.player.repository;

import com.project.G1_T3.player.model.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, UUID> {

    List<PlayerProfile> findTop10ByOrderByCurrentRatingDesc();

    List<PlayerProfile> findAllByOrderByCurrentRatingDesc();

    // Fetch PlayerProfile by user ID
    PlayerProfile findByUserId(UUID id);

    // PlayerProfile getPlayerProfileByUserId(UUID userId);

    PlayerProfile findByProfileId(UUID profileId);

    @Query(value = "select t1.position\n" + //
            "from player_profiles pp1,\n" + //
            "(select \n" + //
            "row_number() over (order by pp2.current_rating desc) as position,\n" + //
            "pp2.user_id uid, pp2.current_rating \n" + //
            "from player_profiles pp2\n" + //
            "order by current_rating desc) as t1\n" + //
            "where t1.uid = pp1.user_id and t1.uid = :userId", nativeQuery = true)
    public Long getPositionOfPlayer(@Param("userId") UUID userId);

}
