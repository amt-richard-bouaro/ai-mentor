package com.rbouaro.aimentor.service;

import com.rbouaro.aimentor.constants.enums.GoalStatus;
import com.rbouaro.aimentor.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantOrchestrator {

    private final MemoryService memoryService;
    private final LLMService llmService;
    private final RoadmapService roadmapService;
    private final ToolService toolService;

    @Transactional
    public String processUserInput(String username, String userInput) {
        log.info("Processing user input from {}: {}", username, userInput);
        
        // Get or create user
        User user = getOrCreateUser(username);
        
        // Check if user has an active goal
        Optional<UserGoal> latestGoalOpt = memoryService.findLatestGoalForUser(user);
        
        if (latestGoalOpt.isEmpty() || latestGoalOpt.get().getStatus() != GoalStatus.ACTIVE) {
            // No active goal, treat input as a new goal
            return handleNewGoal(user, userInput);
        } else {
            // User has an active goal
            UserGoal activeGoal = latestGoalOpt.get();
            
            // Check if the goal has a roadmap
            Optional<Roadmap> roadmapOpt = memoryService.findRoadmapForGoal(activeGoal);
            
            if (roadmapOpt.isEmpty()) {
                // Goal exists but no roadmap yet, treat input as context for roadmap generation
                return handleRoadmapGeneration(activeGoal, userInput);
            } else {
                // Goal and roadmap exist, handle progress updates or questions
                return handleExistingRoadmap(roadmapOpt.get(), userInput);
            }
        }
    }
    
    private User getOrCreateUser(String username) {
        return memoryService.findUserByUsername(username)
                .orElseGet(() -> memoryService.createUser(username, username + "@example.com"));
    }
    
    private String handleNewGoal(User user, String goalDescription) {
        log.info("Handling new goal for user {}: {}", user.getUsername(), goalDescription);
        
        // Create a new goal
        UserGoal goal = memoryService.createGoal(user, extractGoalTitle(goalDescription), goalDescription);
        
        // Generate clarifying questions
        String questions = llmService.generateClarifyingQuestions(goalDescription);
        
        return "I understand you want to " + goal.getTitle() + ". To create a personalized roadmap, I need some more information:\n\n" + questions;
    }
    
    private String extractGoalTitle(String goalDescription) {
        // Extract a short title from the goal description
        // For now, just take the first 50 characters or less
        return goalDescription.length() <= 50 
                ? goalDescription 
                : goalDescription.substring(0, 47) + "...";
    }
    
    private String handleRoadmapGeneration(UserGoal goal, String userContext) {
        log.info("Generating roadmap for goal: {} with context: {}", goal.getTitle(), userContext);
        
        // Generate roadmap
        Roadmap roadmap = roadmapService.generateRoadmap(goal, userContext);
        
        // Get milestones
        List<Milestone> milestones = roadmapService.getMilestonesForRoadmap(roadmap);
        
        // Build response
        StringBuilder response = new StringBuilder();
        response.append("Great! I've created a learning roadmap for you: **").append(roadmap.getTitle()).append("**\n\n");
        response.append(roadmap.getDescription()).append("\n\n");
        response.append("Here are your milestones:\n\n");
        
        for (int i = 0; i < milestones.size(); i++) {
            Milestone milestone = milestones.get(i);
            response.append("**").append(i + 1).append(". ").append(milestone.getTitle()).append("**\n");
            response.append(milestone.getDescription()).append("\n\n");
        }
        
        response.append("You can ask for details about any milestone by saying \"Tell me more about milestone X\" or \"Show resources for milestone X\".");
        
        return response.toString();
    }
    
    private String handleExistingRoadmap(Roadmap roadmap, String userInput) {
        log.info("Handling input for existing roadmap: {}", userInput);
        
        String lowercaseInput = userInput.toLowerCase();
        
        // Check if user is asking about a specific milestone
        if (lowercaseInput.contains("milestone")) {
            return handleMilestoneQuery(roadmap, userInput);
        }
        
        // Check if user is updating progress
        if (lowercaseInput.contains("complete") || lowercaseInput.contains("finished") || lowercaseInput.contains("done")) {
            return handleProgressUpdate(roadmap, userInput);
        }
        
        // Default: show roadmap summary
        return showRoadmapSummary(roadmap);
    }
    
    private String handleMilestoneQuery(Roadmap roadmap, String userInput) {
        List<Milestone> milestones = roadmapService.getMilestonesForRoadmap(roadmap);
        
        // Try to extract milestone number
        int milestoneIndex = extractMilestoneNumber(userInput) - 1;
        
        if (milestoneIndex >= 0 && milestoneIndex < milestones.size()) {
            Milestone milestone = milestones.get(milestoneIndex);
            
            if (userInput.toLowerCase().contains("resource")) {
                // Show resources for the milestone
                return showMilestoneResources(milestone);
            } else {
                // Show milestone details
                return showMilestoneDetails(milestone);
            }
        } else {
            return "I couldn't find that milestone. Please specify a milestone number between 1 and " + milestones.size() + ".";
        }
    }
    
    private int extractMilestoneNumber(String input) {
        // Simple extraction of a number after "milestone"
        try {
            int index = input.toLowerCase().indexOf("milestone");
            if (index >= 0) {
                String afterMilestone = input.substring(index + 9).trim();
                String[] parts = afterMilestone.split("\\s+");
                if (parts.length > 0) {
                    return Integer.parseInt(parts[0]);
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract milestone number from: {}", input);
        }
        return -1;
    }
    
    private String showMilestoneDetails(Milestone milestone) {
        StringBuilder response = new StringBuilder();
        response.append("**Milestone: ").append(milestone.getTitle()).append("**\n\n");
        response.append(milestone.getDescription()).append("\n\n");
        response.append("Status: ").append(milestone.getStatus()).append("\n\n");
        response.append("To see learning resources for this milestone, say \"Show resources for milestone ").append(milestone.getOrderIndex() + 1).append("\".");
        
        return response.toString();
    }
    
    private String showMilestoneResources(Milestone milestone) {
        List<Resource> resources = roadmapService.getResourcesForMilestone(milestone);
        
        StringBuilder response = new StringBuilder();
        response.append("**Resources for ").append(milestone.getTitle()).append(":**\n\n");
        
        if (resources.isEmpty()) {
            response.append("No resources found for this milestone. Let me find some for you...\n\n");
            
            // Generate resources
            roadmapService.generateResourcesForMilestone(milestone);
            resources = roadmapService.getResourcesForMilestone(milestone);
            
            if (resources.isEmpty()) {
                response.append("I couldn't find any resources. Please try again later.");
                return response.toString();
            }
        }
        
        for (int i = 0; i < resources.size(); i++) {
            Resource resource = resources.get(i);
            response.append(i + 1).append(". **").append(resource.getTitle()).append("**\n");
            response.append(resource.getDescription()).append("\n");
            response.append("Type: ").append(resource.getType()).append("\n");
            response.append("URL: ").append(resource.getUrl()).append("\n\n");
        }
        
        response.append("To mark a resource as completed, say \"I completed resource X from milestone ").append(milestone.getOrderIndex() + 1).append("\".");
        
        return response.toString();
    }
    
    private String handleProgressUpdate(Roadmap roadmap, String userInput) {
        String lowercaseInput = userInput.toLowerCase();
        
        if (lowercaseInput.contains("milestone")) {
            // Update milestone status
            int milestoneIndex = extractMilestoneNumber(userInput) - 1;
            List<Milestone> milestones = roadmapService.getMilestonesForRoadmap(roadmap);
            
            if (milestoneIndex >= 0 && milestoneIndex < milestones.size()) {
                Milestone milestone = milestones.get(milestoneIndex);
                roadmapService.updateMilestoneStatus(milestone, Milestone.MilestoneStatus.COMPLETED);
                
                double progress = roadmapService.calculateRoadmapProgress(roadmap);
                
                return "Great job completing milestone " + (milestoneIndex + 1) + "! Your overall progress is now " 
                        + String.format("%.1f", progress) + "%.";
            }
        } else if (lowercaseInput.contains("resource")) {
            // Try to extract milestone and resource numbers
            int milestoneIndex = extractMilestoneNumber(userInput) - 1;
            int resourceIndex = extractResourceNumber(userInput) - 1;
            
            List<Milestone> milestones = roadmapService.getMilestonesForRoadmap(roadmap);
            
            if (milestoneIndex >= 0 && milestoneIndex < milestones.size()) {
                Milestone milestone = milestones.get(milestoneIndex);
                List<Resource> resources = roadmapService.getResourcesForMilestone(milestone);
                
                if (resourceIndex >= 0 && resourceIndex < resources.size()) {
                    Resource resource = resources.get(resourceIndex);
                    roadmapService.updateResourceStatus(resource, Resource.ResourceStatus.COMPLETED);
                    
                    return "Great job completing the resource! Keep up the good work.";
                }
            }
        }
        
        return "I'm not sure which milestone or resource you've completed. Please specify the milestone or resource number.";
    }
    
    private int extractResourceNumber(String input) {
        // Simple extraction of a number after "resource"
        try {
            int index = input.toLowerCase().indexOf("resource");
            if (index >= 0) {
                String afterResource = input.substring(index + 8).trim();
                String[] parts = afterResource.split("\\s+");
                if (parts.length > 0) {
                    return Integer.parseInt(parts[0]);
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract resource number from: {}", input);
        }
        return -1;
    }
    
    private String showRoadmapSummary(Roadmap roadmap) {
        List<Milestone> milestones = roadmapService.getMilestonesForRoadmap(roadmap);
        double progress = roadmapService.calculateRoadmapProgress(roadmap);
        
        StringBuilder response = new StringBuilder();
        response.append("**").append(roadmap.getTitle()).append("**\n\n");
        response.append(roadmap.getDescription()).append("\n\n");
        response.append("Overall progress: ").append(String.format("%.1f", progress)).append("%\n\n");
        response.append("Milestones:\n");
        
        for (int i = 0; i < milestones.size(); i++) {
            Milestone milestone = milestones.get(i);
            String statusIcon = milestone.getStatus() == Milestone.MilestoneStatus.COMPLETED ? "✅" : 
                               (milestone.getStatus() == Milestone.MilestoneStatus.IN_PROGRESS ? "🔄" : "⏳");
            
            response.append(statusIcon).append(" ").append(i + 1).append(". ").append(milestone.getTitle()).append("\n");
        }
        
        response.append("\nYou can ask for details about any milestone by saying \"Tell me more about milestone X\".");
        
        return response.toString();
    }
}