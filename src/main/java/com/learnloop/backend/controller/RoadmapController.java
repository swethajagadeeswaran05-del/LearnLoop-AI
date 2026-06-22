package com.learnloop.backend.controller;

import com.learnloop.backend.service.RoadmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/roadmaps")
public class RoadmapController {

    @Autowired
    private RoadmapService roadmapService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateRoadmap(@RequestBody Map<String, String> request) {
        String targetSkill = request.get("targetSkill");
        if (targetSkill == null || targetSkill.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("targetSkill parameter is required");
        }

        try {
            return ResponseEntity.ok(roadmapService.generateRoadmap(targetSkill));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
