package com.rbouaro.aimentor.service;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.domain.io.UserInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbouaro.aimentor.agent.ContentGenerationAgent;
import com.rbouaro.aimentor.agent.QuizGenerationAgent;
import com.rbouaro.aimentor.agent.ResourceRecommendationAgent;
import com.rbouaro.aimentor.agent.RoadmapGenerationAgent;
import com.rbouaro.aimentor.dto.quiz.QuizQuestionDto;
import com.rbouaro.aimentor.dto.quiz.QuizResponse;
import com.rbouaro.aimentor.dto.quiz.QuizResultQuestionDto;
import com.rbouaro.aimentor.dto.quiz.QuizResultResponse;
import com.rbouaro.aimentor.entity.*;
import com.rbouaro.aimentor.exceptions.ConflictException;
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
                    memoryService.createMilestone(roadmap, milestoneTitle, milestoneDescription, null, orderIndex);
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
                    String thumbnail = resourceNode.path("thumbnail").asText(null);
                    memoryService.createResource(milestone, title, description, url, type, thumbnail);
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

    @Transactional
    public String generateContentForRoadmap(Roadmap roadmap) {
        log.info("[ROADMAP] Generating overview content for roadmapId={} title='{}'", roadmap.getId(), roadmap.getTitle());
        List<Milestone> milestones = memoryService.findMilestonesForRoadmap(roadmap);
        StringBuilder milestoneTitles = new StringBuilder();
        for (int i = 0; i < milestones.size(); i++) {
            milestoneTitles.append(i + 1).append(". ").append(milestones.get(i).getTitle()).append("\n");
        }
        String input = """
                Write an overview for this learning roadmap.

                Title: %s
                Description: %s
                Milestones: %s

                Cover: what it's about, who it's for, expected outcomes, and any prerequisites.
                Keep it concise — 200-300 words.
                """.formatted(roadmap.getTitle(), roadmap.getDescription(), milestoneTitles);
        String tiptap = invokeContentAgent(input);
        memoryService.updateRoadmapContent(roadmap, tiptap);
        log.info("[ROADMAP] Overview content saved for roadmapId={}", roadmap.getId());
        return tiptap;
    }

    @Transactional
    public String generateContentForMilestone(Milestone milestone) {
        log.info("[ROADMAP] Generating content for milestoneId={} title='{}'", milestone.getId(), milestone.getTitle());
        String input = """
                Write an educational article for this learning milestone.

                Milestone: %s
                Description: %s

                Cover: key concepts, why they matter, common pitfalls, and practical tips.
                Keep it concise — 250-350 words.
                """.formatted(milestone.getTitle(), milestone.getDescription());
        String tiptap = invokeContentAgent(input);
        memoryService.updateMilestoneContent(milestone, tiptap);
        log.info("[ROADMAP] Content saved for milestoneId={}", milestone.getId());
        return tiptap;
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

    @Transactional
    public QuizResponse getOrGenerateMilestoneQuiz(Milestone milestone) {
        Quiz quiz = memoryService.findQuizByMilestone(milestone).orElseGet(() -> {
            log.info("[QUIZ] Generating milestone quiz for milestoneId={}", milestone.getId());
            String input = """
                    Generate 4 multiple-choice questions to test understanding of this milestone.

                    Milestone: %s
                    Description: %s
                    """.formatted(milestone.getTitle(), milestone.getDescription());
            String json = invokeQuizAgent(input);
            return memoryService.saveQuiz(Quiz.builder()
                    .milestone(milestone)
                    .questionsJson(json)
                    .build());
        });
        return toQuizResponse(quiz);
    }

    @Transactional
    public QuizResponse getOrGenerateRoadmapQuiz(Roadmap roadmap) {
        int total = memoryService.countMilestones(roadmap);
        int completed = memoryService.countCompletedMilestones(roadmap);
        if (completed < total) {
            throw new ConflictException(
                    "Complete all milestones before taking the final quiz. Progress: " + completed + "/" + total);
        }
        Quiz quiz = memoryService.findQuizByRoadmap(roadmap).orElseGet(() -> {
            log.info("[QUIZ] Generating roadmap quiz for roadmapId={}", roadmap.getId());
            List<Milestone> milestones = memoryService.findMilestonesForRoadmap(roadmap);
            StringBuilder topics = new StringBuilder();
            for (int i = 0; i < milestones.size(); i++) {
                topics.append(i + 1).append(". ").append(milestones.get(i).getTitle()).append("\n");
            }
            String input = """
                    Generate 15 multiple-choice questions covering all topics in this learning roadmap.

                    Roadmap: %s
                    Topics covered:
                    %s
                    Spread the questions evenly across all topics.
                    """.formatted(roadmap.getTitle(), topics);
            String json = invokeQuizAgent(input);
            return memoryService.saveQuiz(Quiz.builder()
                    .roadmap(roadmap)
                    .questionsJson(json)
                    .build());
        });
        return toQuizResponse(quiz);
    }

    @Transactional
    public QuizResultResponse evaluateMilestoneQuiz(Quiz quiz, Milestone milestone, List<Integer> answers) {
        QuizResultResponse result = evaluate(quiz, answers);
        if (result.passed()) {
            log.info("[QUIZ] Milestone quiz passed — marking milestoneId={} as COMPLETED", milestone.getId());
            memoryService.updateMilestoneStatus(milestone, Milestone.MilestoneStatus.COMPLETED);
        }
        return result;
    }

    @Transactional
    public QuizResultResponse evaluateRoadmapQuiz(Quiz quiz, Roadmap roadmap, List<Integer> answers) {
        QuizResultResponse result = evaluate(quiz, answers);
        if (result.passed()) {
            log.info("[QUIZ] Roadmap quiz passed — marking goal as COMPLETED for roadmapId={}", roadmap.getId());
            memoryService.completeGoal(roadmap.getUserGoal());
        }
        return result;
    }

    private QuizResultResponse evaluate(Quiz quiz, List<Integer> answers) {
        try {
            JsonNode root = objectMapper.readTree(quiz.getQuestionsJson());
            JsonNode questions = root.path("questions");
            int total = questions.size();
            int score = 0;
            List<QuizResultQuestionDto> resultQuestions = new java.util.ArrayList<>();

            for (int i = 0; i < total; i++) {
                JsonNode q = questions.get(i);
                int correctIndex = q.path("correctIndex").asInt();
                int selectedIndex = (i < answers.size()) ? answers.get(i) : -1;
                boolean correct = selectedIndex == correctIndex;
                if (correct) score++;

                List<String> options = new java.util.ArrayList<>();
                q.path("options").forEach(o -> options.add(o.asText()));

                resultQuestions.add(new QuizResultQuestionDto(
                        q.path("question").asText(),
                        options,
                        correctIndex,
                        selectedIndex,
                        correct,
                        q.path("explanation").asText()
                ));
            }

            double percentage = total == 0 ? 0 : Math.round((double) score / total * 1000.0) / 10.0;
            boolean passed = percentage >= 60.0;
            return new QuizResultResponse(score, total, percentage, passed, resultQuestions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to evaluate quiz", e);
        }
    }

    private QuizResponse toQuizResponse(Quiz quiz) {
        try {
            JsonNode root = objectMapper.readTree(quiz.getQuestionsJson());
            JsonNode questions = root.path("questions");
            List<QuizQuestionDto> dtos = new java.util.ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                JsonNode q = questions.get(i);
                List<String> options = new java.util.ArrayList<>();
                q.path("options").forEach(o -> options.add(o.asText()));
                dtos.add(new QuizQuestionDto(i, q.path("question").asText(), options));
            }
            return new QuizResponse(quiz.getId(), dtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to read quiz", e);
        }
    }

    private String invokeQuizAgent(String input) {
        String raw = AgentInvocation
                .create(agentPlatform, QuizGenerationAgent.QuizJson.class)
                .invoke(new UserInput(input))
                .content();
        String cleaned = cleanJson(raw);
        try {
            objectMapper.readTree(cleaned);
            return cleaned;
        } catch (JsonProcessingException e) {
            String repaired = repairJson(cleaned);
            try {
                objectMapper.readTree(repaired);
                return repaired;
            } catch (JsonProcessingException ex) {
                log.error("[QUIZ] Quiz agent returned invalid JSON");
                throw new RuntimeException("Failed to generate quiz: invalid JSON", ex);
            }
        }
    }

    private String invokeContentAgent(String input) {
        String raw = AgentInvocation
                .create(agentPlatform, ContentGenerationAgent.TiptapContent.class)
                .invoke(new UserInput(input))
                .content();
        String cleaned = cleanJson(raw);
        try {
            objectMapper.readTree(cleaned);
            return cleaned;
        } catch (JsonProcessingException e) {
            String repaired = repairJson(cleaned);
            try {
                objectMapper.readTree(repaired);
                return repaired;
            } catch (JsonProcessingException ex) {
                log.warn("[ROADMAP] Content agent returned invalid JSON, storing raw text as paragraph");
                return fallbackTiptap(raw);
            }
        }
    }

    private static String fallbackTiptap(String text) {
        String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"content\":[{\"type\":\"text\",\"text\":\"" + escaped + "\"}]}]}";
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