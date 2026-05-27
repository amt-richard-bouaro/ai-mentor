package com.rbouaro.aimentor.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.rbouaro.aimentor.documentation.MilestoneApi;
import com.rbouaro.aimentor.dto.Views;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.milestone.MilestoneResponse;
import com.rbouaro.aimentor.dto.milestone.UpdateMilestoneStatusRequest;
import com.rbouaro.aimentor.dto.quiz.QuizResponse;
import com.rbouaro.aimentor.dto.quiz.QuizResultResponse;
import com.rbouaro.aimentor.dto.quiz.QuizSubmitRequest;
import com.rbouaro.aimentor.dto.resource.ResourceResponse;
import com.rbouaro.aimentor.dto.resource.UpdateResourceStatusRequest;
import com.rbouaro.aimentor.entity.Quiz;
import com.rbouaro.aimentor.entity.Milestone;
import com.rbouaro.aimentor.entity.Resource;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.exceptions.NotFoundException;
import com.rbouaro.aimentor.mapper.MilestoneMapper;
import com.rbouaro.aimentor.mapper.ResourceMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.rbouaro.aimentor.service.MemoryService;
import com.rbouaro.aimentor.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController implements MilestoneApi {

    private final MemoryService memoryService;
    private final RoadmapService roadmapService;
    private final MilestoneMapper milestoneMapper;
    private final ResourceMapper resourceMapper;

    @JsonView(Views.Detail.class)
    @Override
    public AppResponse<MilestoneResponse> getMilestone(User user, UUID id) {
        log.info("[MILESTONE-CTRL] getMilestone userId={} milestoneId={}", user.getId(), id);
        Milestone milestone = memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));
        if (milestone.getContent() == null) {
            log.info("[MILESTONE-CTRL] Content not yet generated — generating on-demand for milestoneId={}", id);
            String content = roadmapService.generateContentForMilestone(milestone);
            milestone.setContent(content);
        }
        return new AppResponse<>("OK", milestoneMapper.toResponse(milestone));
    }

    @JsonView(Views.Summary.class)
    @Override
    public AppResponse<MilestoneResponse> updateMilestoneStatus(User user, UUID id, UpdateMilestoneStatusRequest request) {
        log.info("[MILESTONE-CTRL] updateMilestoneStatus userId={} milestoneId={} status={}", user.getId(), id, request.status());
        Milestone milestone = memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));
        roadmapService.updateMilestoneStatus(milestone, request.status());
        milestone.setStatus(request.status());
        return new AppResponse<>("OK", milestoneMapper.toResponse(milestone));
    }

    @Override
    public AppResponse<List<ResourceResponse>> listResources(User user, UUID id) {
        log.info("[MILESTONE-CTRL] listResources userId={} milestoneId={}", user.getId(), id);
        Milestone milestone = memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));

        List<Resource> resources = roadmapService.getResourcesForMilestone(milestone);
        if (resources.isEmpty()) {
            log.info("[MILESTONE-CTRL] No resources — generating on-demand for milestoneId={}", id);
            roadmapService.generateResourcesForMilestone(milestone);
            resources = roadmapService.getResourcesForMilestone(milestone);
        }

        return new AppResponse<>("OK", resourceMapper.toResponseList(resources));
    }

    @GetMapping("/{id}/quiz")
    public AppResponse<QuizResponse> getQuiz(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        log.info("[MILESTONE-CTRL] getQuiz userId={} milestoneId={}", user.getId(), id);
        Milestone milestone = memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));
        return new AppResponse<>("OK", roadmapService.getOrGenerateMilestoneQuiz(milestone));
    }

    @PostMapping("/{id}/quiz/submit")
    public AppResponse<QuizResultResponse> submitQuiz(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody QuizSubmitRequest request) {
        log.info("[MILESTONE-CTRL] submitQuiz userId={} milestoneId={}", user.getId(), id);
        Milestone milestone = memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));
        Quiz quiz = memoryService.findQuizByMilestone(milestone)
                .orElseThrow(() -> new NotFoundException("Quiz not found — fetch GET /{id}/quiz first"));
        return new AppResponse<>("OK", roadmapService.evaluateMilestoneQuiz(quiz, milestone, request.answers()));
    }

    @Override
    public AppResponse<ResourceResponse> updateResourceStatus(User user, UUID id, UUID resourceId, UpdateResourceStatusRequest request) {
        log.info("[MILESTONE-CTRL] updateResourceStatus userId={} milestoneId={} resourceId={} status={}", user.getId(), id, resourceId, request.status());
        memoryService.findMilestoneByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Milestone not found: " + id));
        Resource resource = memoryService.findResourceByIdForUser(resourceId, user)
                .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));
        roadmapService.updateResourceStatus(resource, request.status());
        resource.setStatus(request.status());
        return new AppResponse<>("OK", resourceMapper.toResponse(resource));
    }
}