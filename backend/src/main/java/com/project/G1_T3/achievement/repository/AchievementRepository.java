package com.project.G1_T3.achievement.repository;

import com.project.G1_T3.achievement.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByCriteriaType(String criteriaType);
}
