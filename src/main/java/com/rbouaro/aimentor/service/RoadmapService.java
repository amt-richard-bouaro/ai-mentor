package com.rbouaro.aimentor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbouaro.aimentor.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapService {

    private final LLMService llmService;
    private final MemoryService memoryService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Roadmap generateRoadmap(UserGoal userGoal, String userContext) {
        log.info("Generating roadmap for goal: {}", userGoal.getTitle());
        
        String roadmapJson = llmService.generateRoadmap(userGoal.getTitle(), userContext);
        
        try {
            JsonNode roadmapNode = objectMapper.readTree(roadmapJson);
            
            String title = roadmapNode.path("title").asText("Learning Roadmap");
            String description = roadmapNode.path("description").asText("A personalized learning roadmap");
            
            // Create the roadmap
            Roadmap roadmap = memoryService.createRoadmap(userGoal, title, description);
            
            // Create milestones
            JsonNode milestonesNode = roadmapNode.path("milestones");
            if (milestonesNode.isArray()) {
                int orderIndex = 0;
                for (JsonNode milestoneNode : milestonesNode) {
                    String milestoneTitle = milestoneNode.path("title").asText("Milestone " + (orderIndex + 1));
                    String milestoneDescription = milestoneNode.path("description").asText("");
                    
                    Milestone milestone = memoryService.createMilestone(roadmap, milestoneTitle, milestoneDescription, orderIndex);
                    
                    // Generate resources for this milestone
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
        
        String resourcesJson = llmService.generateResourceRecommendations(
                milestone.getTitle(), milestone.getDescription());
        
        try {
            JsonNode rootNode = objectMapper.readTree(resourcesJson);
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
                    
                    // For now, we'll use the title as the URL since we don't have real URLs yet
                    // In a real implementation, we would use the ToolService to fetch actual URLs
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
}