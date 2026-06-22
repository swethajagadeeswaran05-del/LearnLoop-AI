package com.learnloop.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RoadmapService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> generateRoadmap(String targetSkill) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("target_skill", targetSkill);

        try {
            String url = aiServiceUrl + "/roadmap/generate";
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            System.err.println("Error calling AI service roadmap endpoint: " + e.getMessage());
        }

        // Fallback local roadmap generator
        return getLocalRoadmapFallback(targetSkill);
    }

    private Map<String, Object> getLocalRoadmapFallback(String targetSkill) {
        Map<String, Object> roadmap = new HashMap<>();
        roadmap.put("target_skill", targetSkill);
        roadmap.put("estimated_duration", "4-6 Weeks");

        List<Map<String, Object>> steps = new ArrayList<>();

        // Step 1
        Map<String, Object> step1 = new HashMap<>();
        step1.put("step", 1);
        step1.put("title", "Foundations of " + targetSkill);
        step1.put("description", "Get familiar with the core syntax, terminology, and tools required for " + targetSkill + ".");
        step1.put("duration", "Week 1");
        List<Map<String, String>> res1 = new ArrayList<>();
        res1.add(Map.of("title", "Official Documentation", "url", "https://docs.google.com"));
        res1.add(Map.of("title", targetSkill + " Crash Course (YouTube)", "url", "https://www.youtube.com/results?search_query=" + targetSkill + "+for+beginners"));
        step1.put("resources", res1);
        steps.add(step1);

        // Step 2
        Map<String, Object> step2 = new HashMap<>();
        step2.put("step", 2);
        step2.put("title", "Intermediate Concepts");
        step2.put("description", "Deep dive into building simple applications, data structures, and best practices.");
        step2.put("duration", "Week 2-3");
        List<Map<String, String>> res2 = new ArrayList<>();
        res2.add(Map.of("title", targetSkill + " Medium Guide", "url", "https://medium.com/search?q=" + targetSkill));
        step2.put("resources", res2);
        steps.add(step2);

        // Step 3
        Map<String, Object> step3 = new HashMap<>();
        step3.put("step", 3);
        step3.put("title", "Advanced Features & Capstone Project");
        step3.put("description", "Deploy a complete project, implement security features, optimization, and integrations.");
        step3.put("duration", "Week 4+");
        List<Map<String, String>> res3 = new ArrayList<>();
        res3.add(Map.of("title", targetSkill + " Project ideas on GitHub", "url", "https://github.com/search?q=" + targetSkill + "+projects"));
        step3.put("resources", res3);
        steps.add(step3);

        roadmap.put("steps", steps);
        return roadmap;
    }
}
