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
        log.info("Generating roadmap for goal: {}", userGoal.getTitle());

        String input = "Goal: " + userGoal.getTitle() + "\n\nAdditional context from the user:\n" + userContext;
        String roadmapJson = AgentInvocation
                .create(agentPlatform, RoadmapGenerationAgent.RoadmapJson.class)
                .invoke(new UserInput(input))
                .content();

        String cleanedJson = cleanJson(roadmapJson);

        try {
            JsonNode roadmapNode = objectMapper.readTree(cleanedJson);

            String title = roadmapNode.path("title").asText("Learning Roadmap");
            String description = roadmapNode.path("description").asText("A personalized learning roadmap");

            Roadmap roadmap = memoryService.createRoadmap(userGoal, title, description);

            JsonNode milestonesNode = roadmapNode.path("milestones");
            if (milestonesNode.isArray()) {
                int orderIndex = 0;
                for (JsonNode milestoneNode : milestonesNode) {
                    String milestoneTitle = milestoneNode.path("title").asText("Milestone " + (orderIndex + 1));
                    String milestoneDescription = milestoneNode.path("description").asText("");
                    Milestone milestone = memoryService.createMilestone(roadmap, milestoneTitle, milestoneDescription, orderIndex);
                    generateResourcesForMilestone(milestone);
                    orderIndex++;
                }
            }

            return roadmap;
        } catch (JsonProcessingException e) {
            log.error("Error parsing roadmap JSON: {}", roadmapJson, e);
            throw new RuntimeException("Failed to parse roadmap JSON", e);
        }
    }

    @Transactional
    public void generateResourcesForMilestone(Milestone milestone) {
        log.info("Generating resources for milestone: {}", milestone.getTitle());

        String input = "Milestone: " + milestone.getTitle() + "\n\nDescription: " + milestone.getDescription();
        String resourcesJson = AgentInvocation
                .create(agentPlatform, ResourceRecommendationAgent.ResourceRecommendations.class)
                .invoke(new UserInput(input))
                .content();

        try {
            JsonNode rootNode = objectMapper.readTree(repairJson(resourcesJson));
            JsonNode resourcesNode = rootNode.path("resources");

            if (resourcesNode.isArray()) {
                for (JsonNode resourceNode : resourcesNode) {
                    String title = resourceNode.path("title").asText("Resource");
                    String description = resourceNode.path("description").asText("");
                    String typeStr = resourceNode.path("type").asText("OTHER");

                    Resource.ResourceType type;
                    try {
                        type = Resource.ResourceType.valueOf(typeStr);
                    } catch (IllegalArgumentException e) {
                        type = Resource.ResourceType.OTHER;
                    }

                    String url = "#" + title.toLowerCase().replace(' ', '-');
                    memoryService.createResource(milestone, title, description, url, type);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing resources JSON: {}", resourcesJson, e);
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
