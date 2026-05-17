package com.rbouaro.aimentor.listener;

import com.rbouaro.aimentor.event.GoalCreatedEvent;
import com.rbouaro.aimentor.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoadmapGenerationListener {

    private final RoadmapService roadmapService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGoalCreated(GoalCreatedEvent event) {
        var userGoal = event.userGoal();
        log.info("[ROADMAP-LISTENER] Generating roadmap for goalId={} title='{}'", userGoal.getId(), userGoal.getTitle());
        try {
            roadmapService.generateRoadmap(userGoal, userGoal.getDescription() != null ? userGoal.getDescription() : "");
            log.info("[ROADMAP-LISTENER] Roadmap generation complete for goalId={}", userGoal.getId());
        } catch (Exception e) {
            log.error("[ROADMAP-LISTENER] Roadmap generation failed for goalId={}", userGoal.getId(), e);
        }
    }
}