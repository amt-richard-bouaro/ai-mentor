package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Milestone;
import com.rbouaro.aimentor.entity.Resource;
import com.rbouaro.aimentor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByMilestone(Milestone milestone);

    List<Resource> findByMilestoneAndType(Milestone milestone, Resource.ResourceType type);

    List<Resource> findByMilestoneAndStatus(Milestone milestone, Resource.ResourceStatus status);

    int countByMilestone(Milestone milestone);

    int countByMilestoneAndStatus(Milestone milestone, Resource.ResourceStatus status);

    Optional<Resource> findByIdIsAndMilestone_Roadmap_UserGoal_User(UUID id, User user);
}