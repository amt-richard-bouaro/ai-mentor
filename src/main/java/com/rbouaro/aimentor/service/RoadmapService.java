package com.rbouaro.aimentor.service;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.domain.io.UserInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbouaro.aimentor.agent.ResourceRecommendationAgent;
import com.rbouaro.aimentor.agent.RoadmapGenerationAgent;
import com.rbouaro.aimentor.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapService {

    private final AgentPlatform agentPlatform;
    private final MemoryService memoryService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Roadmap generateRoadmap(UserGoal userGoal, String userContext) {
        log.info("[ROADMAP] generateRoadmap goalId={} title='{}'", userGoal.getId(), userGoal.getTitle());

        String input = "Goal: " + userGoal.getTitle() + "\n\nAdditional context from the user:\n" + userContext;
        log.info("[ROADMAP] Invoking RoadmapGenerationAgent goalId={}", userGoal.getId());
        String roadmapJson = AgentInvocation
                .create(agentPlatform, RoadmapGenerationAgent.RoadmapJson.class)
                .invoke(new UserInput(input))
                .content();
        log.info("[ROADMAP] RoadmapGenerationAgent completed goalId={} responseLength={}", userGoal.getId(), roadmapJson.length());

        String cleanedJson = cleanJson(roadmapJson);

        try {
            JsonNode roadmapNode = objectMapper.readTree(cleanedJson);

            String title = roadmapNode.path("title").asText("Learning Roadmap");
            String description = roadmapNode.path("description").asText("A personalized learning roadmap");

            Roadmap roadmap = memoryService.createRoadmap(userGoal, title, description);
            log.debug("[ROADMAP] Created roadmap id={} title='{}'", roadmap.getId(), roadmap.getTitle());

            JsonNode milestonesNode = roadmapNode.path("milestones");
            if (milestonesNode.isArray()) {
                int orderIndex = 0;
                for (JsonNode milestoneNode : milestonesNode) {
                    String milestoneTitle = milestoneNode.path("title").asText("Milestone " + (orderIndex + 1));
                    String milestoneDescription = milestoneNode.path("description").asText("");
                    memoryService.createMilestone(roadmap, milestoneTitle, milestoneDescription, orderIndex);
                    log.debug("[ROADMAP] Created milestone index={} title='{}'", orderIndex, milestoneTitle);
                    orderIndex++;
                }
                log.info("[ROADMAP] Saved {} milestones for roadmapId={}", orderIndex, roadmap.getId());
            }

            return roadmap;
        } catch (JsonProcessingException e) {
            log.error("[ROADMAP] Failed to parse roadmap JSON goalId={} raw='{}'", userGoal.getId(), roadmapJson, e);
            throw new RuntimeException("Failed to parse roadmap JSON", e);
        }
    }

    @Transactional
    public void generateResourcesForMilestone(Milestone milestone) {
        log.info("[ROADMAP] generateResourcesForMilestone milestoneId={} title='{}'", milestone.getId(), milestone.getTitle());

        String input = "Milestone: " + milestone.getTitle() + "\n\nDescription: " + milestone.getDescription();
        log.info("[ROADMAP] Invoking ResourceRecommendationAgent milestoneId={}", milestone.getId());
        String resourcesJson = AgentInvocation
                .create(agentPlatform, ResourceRecommendationAgent.ResourceRecommendations.class)
                .invoke(new UserInput(input))
                .content();
        log.info("[ROADMAP] ResourceRecommendationAgent completed milestoneId={} responseLength={}", milestone.getId(), resourcesJson.length());

        try {
            JsonNode rootNode = objectMapper.readTree(repairJson(resourcesJson));
            JsonNode resourcesNode = rootNode.path("resources");

            if (resourcesNode.isArray()) {
                int count = 0;
                for (JsonNode resourceNode : resourcesNode) {
                    String title = resourceNode.path("title").asText("Resource");
                    String description = resourceNode.path("description").asText("");
                    String typeStr = resourceNode.path("type").asText("OTHER");

                    Resource.ResourceType type;
                    try {
                        type = Resource.ResourceType.valueOf(typeStr);
                    } catch (IllegalArgumentException e) {
                        log.warn("[ROADMAP] Unknown resource type='{}' — defaulting to OTHER", typeStr);
                        type = Resource.ResourceType.OTHER;
                    }

                    String url = resourceNode.path("url").asText("");
                    memoryService.createResource(milestone, title, description, url, type);
                    log.debug("[ROADMAP] Saved resource title='{}' type={} milestoneId={}", title, type, milestone.getId());
                    count++;
                }
                log.info("[ROADMAP] Saved {} resources for milestoneId={}", count, milestone.getId());
            }
        } catch (JsonProcessingException e) {
            log.error("[ROADMAP] Failed to parse resources JSON milestoneId={} raw='{}'", milestone.getId(), resourcesJson, e);
            throw new RuntimeException("Failed to parse resources JSON", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Milestone> getMilestonesForRoadmap(Roadmap roadmap) {
        return memoryService.findMilestonesForRoadmap(roadmap);
    }

    @Transactional(readOnly = true)
    public List<Resource> getResourcesForMilestone(Milestone milestone) {
        return memoryService.findResourcesForMilestone(milestone);
    }

    @Transactional
    public void updateMilestoneStatus(Milestone milestone, Milestone.MilestoneStatus status) {
        memoryService.updateMilestoneStatus(milestone, status);
    }

    @Transactional
    public void updateResourceStatus(Resource resource, Resource.ResourceStatus status) {
        memoryService.updateResourceStatus(resource, status);
    }

    @Transactional(readOnly = true)
    public double calculateRoadmapProgress(Roadmap roadmap) {
        return memoryService.calculateRoadmapProgress(roadmap);
    }

    private static String cleanJson(String json) {
        String cleaned = json.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }

    private static String repairJson(String json) {
        long openBraces = json.chars().filter(ch -> ch == '{').count();
        long closeBraces = json.chars().filter(ch -> ch == '}').count();
        long openBrackets = json.chars().filter(ch -> ch == '[').count();
        long closeBrackets = json.chars().filter(ch -> ch == ']').count();

        StringBuilder repaired = new StringBuilder(json);
        for (int i = 0; i < openBraces - closeBraces; i++) repaired.append('}');
        for (int i = 0; i < openBrackets - closeBrackets; i++) repaired.append(']');
        return repaired.toString();
    }
}