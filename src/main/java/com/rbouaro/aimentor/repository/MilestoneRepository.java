package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Milestone;
import com.rbouaro.aimentor.entity.Roadmap;
import com.rbouaro.aimentor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByRoadmap(Roadmap roadmap);

    List<Milestone> findByRoadmapOrderByOrderIndexAsc(Roadmap roadmap);

    List<Milestone> findByRoadmapAndStatus(Roadmap roadmap, Milestone.MilestoneStatus status);

    int countByRoadmap(Roadmap roadmap);

    int countByRoadmapAndStatus(Roadmap roadmap, Milestone.MilestoneStatus status);

    Optional<Milestone> findByIdIsAndRoadmap_UserGoal_User(UUID id, User user);
}