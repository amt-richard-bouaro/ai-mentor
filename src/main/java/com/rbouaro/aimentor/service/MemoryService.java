package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.constants.enums.GoalStatus;
import com.rbouaro.aimentor.entity.*;
import com.rbouaro.aimentor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final RoadmapRepository roadmapRepository;
    private final MilestoneRepository milestoneRepository;
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserGoal> findLatestGoalForUser(User user) {
        return userGoalRepository.findTopByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public UserGoal createGoal(User user, String title, String description) {
        UserGoal userGoal = UserGoal.builder()
                .user(user)
                .title(title)
                .description(description)
                .status(GoalStatus.ACTIVE)
                .build();
        return userGoalRepository.save(userGoal);
    }

    @Transactional(readOnly = true)
    public Optional<Roadmap> findRoadmapForGoal(UserGoal userGoal) {
        return roadmapRepository.findByUserGoal(userGoal);
    }

    @Transactional
    public Roadmap createRoadmap(UserGoal userGoal, String title, String description) {
        Roadmap roadmap = Roadmap.builder()
                .userGoal(userGoal)
                .title(title)
                .description(description)
                .build();
        return roadmapRepository.save(roadmap);
    }

    @Transactional(readOnly = true)
    public List<Milestone> findMilestonesForRoadmap(Roadmap roadmap) {
        return milestoneRepository.findByRoadmapOrderByOrderIndexAsc(roadmap);
    }

    @Transactional
    public Milestone createMilestone(Roadmap roadmap, String title, String description, int orderIndex) {
        Milestone milestone = Milestone.builder()
                .roadmap(roadmap)
                .title(title)
                .description(description)
                .orderIndex(orderIndex)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build();
        return milestoneRepository.save(milestone);
    }

    @Transactional(readOnly = true)
    public List<Resource> findResourcesForMilestone(Milestone milestone) {
        return resourceRepository.findByMilestone(milestone);
    }

    @Transactional
    public Resource createResource(Milestone milestone, String title, String description, 
                                  String url, Resource.ResourceType type) {
        Resource resource = Resource.builder()
                .milestone(milestone)
                .title(title)
                .description(description)
                .url(url)
                .type(type)
                .status(Resource.ResourceStatus.NOT_STARTED)
                .build();
        return resourceRepository.save(resource);
    }

    @Transactional
    public void updateMilestoneStatus(Milestone milestone, Milestone.MilestoneStatus status) {
        milestone.setStatus(status);
        milestoneRepository.save(milestone);
    }

    @Transactional
    public void updateResourceStatus(Resource resource, Resource.ResourceStatus status) {
        resource.setStatus(status);
        resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public double calculateRoadmapProgress(Roadmap roadmap) {
        int totalMilestones = milestoneRepository.countByRoadmap(roadmap);
        if (totalMilestones == 0) {
            return 0.0;
        }
        
        int completedMilestones = milestoneRepository.countByRoadmapAndStatus(roadmap, Milestone.MilestoneStatus.COMPLETED);
        return (double) completedMilestones / totalMilestones * 100.0;
    }
}