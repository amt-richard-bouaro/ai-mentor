package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.constants.enums.GoalStatus;
import com.rbouaro.aimentor.constants.enums.UserPermission;
import com.rbouaro.aimentor.entity.Milestone;
import com.rbouaro.aimentor.entity.Quiz;
import com.rbouaro.aimentor.entity.Resource;
import com.rbouaro.aimentor.entity.Roadmap;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import com.rbouaro.aimentor.event.GoalCreatedEvent;

import com.rbouaro.aimentor.repository.MilestoneRepository;
import com.rbouaro.aimentor.repository.QuizRepository;
import com.rbouaro.aimentor.repository.ResourceRepository;
import com.rbouaro.aimentor.repository.RoadmapRepository;
import com.rbouaro.aimentor.repository.UserGoalRepository;
import com.rbouaro.aimentor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final RoadmapRepository roadmapRepository;
    private final MilestoneRepository milestoneRepository;
    private final ResourceRepository resourceRepository;
    private final QuizRepository quizRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<Roadmap> findAllRoadmapsForUser(User user) {
        return roadmapRepository.findByUserGoal_User(user);
    }

    @Transactional(readOnly = true)
    public Optional<Roadmap> findRoadmapByIdForUser(UUID id, User user) {
        return roadmapRepository.findByIdIsAndUserGoal_User(id, user);
    }

    @Transactional(readOnly = true)
    public Optional<Milestone> findMilestoneByIdForUser(UUID id, User user) {
        return milestoneRepository.findByIdIsAndRoadmap_UserGoal_User(id, user);
    }

    @Transactional(readOnly = true)
    public Optional<Resource> findResourceByIdForUser(UUID id, User user) {
        return resourceRepository.findByIdIsAndMilestone_Roadmap_UserGoal_User(id, user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("password")
                .permissions(Set.of(UserPermission.USER))
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
        UserGoal saved = userGoalRepository.save(userGoal);
        eventPublisher.publishEvent(new GoalCreatedEvent(saved));
        return saved;
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

    @Transactional
    public void updateRoadmapContent(Roadmap roadmap, String content) {
        roadmap.setContent(content);
        roadmapRepository.save(roadmap);
    }

    @Transactional
    public void updateMilestoneContent(Milestone milestone, String content) {
        milestone.setContent(content);
        milestoneRepository.save(milestone);
    }

    @Transactional(readOnly = true)
    public List<Milestone> findMilestonesForRoadmap(Roadmap roadmap) {
        return milestoneRepository.findByRoadmapOrderByOrderIndexAsc(roadmap);
    }

    @Transactional
    public Milestone createMilestone(Roadmap roadmap, String title, String description, String content, int orderIndex) {
        Milestone milestone = Milestone.builder()
                .roadmap(roadmap)
                .title(title)
                .description(description)
                .content(content)
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
                                   String url, Resource.ResourceType type, String thumbnail) {
        Resource resource = Resource.builder()
                .milestone(milestone)
                .title(title)
                .description(description)
                .url(url)
                .thumbnail(thumbnail)
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
    public java.util.Optional<Quiz> findQuizByMilestone(Milestone milestone) {
        return quizRepository.findByMilestone(milestone);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Quiz> findQuizByRoadmap(Roadmap roadmap) {
        return quizRepository.findByRoadmap(roadmap);
    }

    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Transactional
    public void completeGoal(UserGoal userGoal) {
        userGoal.setStatus(com.rbouaro.aimentor.constants.enums.GoalStatus.COMPLETED);
        userGoalRepository.save(userGoal);
    }

    @Transactional(readOnly = true)
    public int countMilestones(Roadmap roadmap) {
        return milestoneRepository.countByRoadmap(roadmap);
    }

    @Transactional(readOnly = true)
    public int countCompletedMilestones(Roadmap roadmap) {
        return milestoneRepository.countByRoadmapAndStatus(roadmap, Milestone.MilestoneStatus.COMPLETED);
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