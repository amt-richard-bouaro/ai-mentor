package com.rbouaro.aimentor.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.rbouaro.aimentor.documentation.RoadmapApi;
import com.rbouaro.aimentor.dto.Views;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.milestone.MilestoneResponse;
import com.rbouaro.aimentor.dto.quiz.QuizResponse;
import com.rbouaro.aimentor.dto.quiz.QuizResultResponse;
import com.rbouaro.aimentor.dto.quiz.QuizSubmitRequest;
import com.rbouaro.aimentor.dto.roadmap.RoadmapResponse;
import com.rbouaro.aimentor.entity.Quiz;
import com.rbouaro.aimentor.entity.Roadmap;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.rbouaro.aimentor.mapper.MilestoneMapper;
import com.rbouaro.aimentor.mapper.RoadmapMapper;
import com.rbouaro.aimentor.service.MemoryService;
import com.rbouaro.aimentor.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/roadmaps")
@RequiredArgsConstructor
public class RoadmapController implements RoadmapApi {

    private final MemoryService memoryService;
    private final RoadmapService roadmapService;
    private final RoadmapMapper roadmapMapper;
    private final MilestoneMapper milestoneMapper;

    @JsonView(Views.Summary.class)
    @Override
    public AppResponse<List<RoadmapResponse>> listRoadmaps(User user) {
        log.info("[ROADMAP-CTRL] listRoadmaps userId={}", user.getId());
        List<RoadmapResponse> roadmaps = roadmapMapper.toResponseList(
                memoryService.findAllRoadmapsForUser(user));
        return new AppResponse<>("OK", roadmaps);
    }

    @JsonView(Views.Detail.class)
    @Override
    public AppResponse<RoadmapResponse> getRoadmap(User user, UUID id) {
        log.info("[ROADMAP-CTRL] getRoadmap userId={} roadmapId={}", user.getId(), id);
        Roadmap roadmap = memoryService.findRoadmapByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        if (roadmap.getContent() == null) {
            log.info("[ROADMAP-CTRL] Content not yet generated — generating on-demand for roadmapId={}", id);
            String content = roadmapService.generateContentForRoadmap(roadmap);
            roadmap.setContent(content);
        }
        return new AppResponse<>("OK", roadmapMapper.toResponse(roadmap));
    }

    @JsonView(Views.Summary.class)
    @Override
    public AppResponse<List<MilestoneResponse>> listMilestones(User user, UUID id) {
        log.info("[ROADMAP-CTRL] listMilestones userId={} roadmapId={}", user.getId(), id);
        Roadmap roadmap = memoryService.findRoadmapByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        List<MilestoneResponse> milestones = milestoneMapper.toResponseList(
                roadmapService.getMilestonesForRoadmap(roadmap));
        return new AppResponse<>("OK", milestones);
    }

    @GetMapping("/{id}/quiz")
    public AppResponse<QuizResponse> getQuiz(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        log.info("[ROADMAP-CTRL] getQuiz userId={} roadmapId={}", user.getId(), id);
        Roadmap roadmap = memoryService.findRoadmapByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        return new AppResponse<>("OK", roadmapService.getOrGenerateRoadmapQuiz(roadmap));
    }

    @PostMapping("/{id}/quiz/submit")
    public AppResponse<QuizResultResponse> submitQuiz(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody QuizSubmitRequest request) {
        log.info("[ROADMAP-CTRL] submitQuiz userId={} roadmapId={}", user.getId(), id);
        Roadmap roadmap = memoryService.findRoadmapByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        Quiz quiz = memoryService.findQuizByRoadmap(roadmap)
                .orElseThrow(() -> new NotFoundException("Quiz not found — fetch GET /{id}/quiz first"));
        return new AppResponse<>("OK", roadmapService.evaluateRoadmapQuiz(quiz, roadmap, request.answers()));
    }

    @Override
    public AppResponse<Double> getProgress(User user, UUID id) {
        log.info("[ROADMAP-CTRL] getProgress userId={} roadmapId={}", user.getId(), id);
        Roadmap roadmap = memoryService.findRoadmapByIdForUser(id, user)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        return new AppResponse<>("OK", roadmapService.calculateRoadmapProgress(roadmap));
    }
}