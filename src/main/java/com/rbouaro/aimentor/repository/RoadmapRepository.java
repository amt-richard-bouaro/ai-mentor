package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Roadmap;
import com.rbouaro.aimentor.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    
    Optional<Roadmap> findByUserGoal(UserGoal userGoal);
    
    boolean existsByUserGoal(UserGoal userGoal);
}