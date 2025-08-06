package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.model.Milestone;
import com.rbouaro.aimentor.model.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    List<Milestone> findByRoadmap(Roadmap roadmap);
    
    List<Milestone> findByRoadmapOrderByOrderIndexAsc(Roadmap roadmap);
    
    List<Milestone> findByRoadmapAndStatus(Roadmap roadmap, Milestone.MilestoneStatus status);
    
    int countByRoadmap(Roadmap roadmap);
    
    int countByRoadmapAndStatus(Roadmap roadmap, Milestone.MilestoneStatus status);
}