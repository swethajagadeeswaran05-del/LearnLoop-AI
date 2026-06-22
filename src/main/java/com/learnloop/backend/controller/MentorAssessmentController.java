package com.learnloop.backend.controller;

import com.learnloop.backend.model.MentorAssessment;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.MentorAssessmentService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mentor-assessments")
public class MentorAssessmentController {

    @Autowired
    private MentorAssessmentService assessmentService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @PostMapping
    public ResponseEntity<?> createAssessment(@RequestBody MentorAssessment assessment) {
        try {
            User mentor = getCurrentUser();
            return ResponseEntity.ok(assessmentService.createAssessment(mentor, assessment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseAssessments(@PathVariable String courseId) {
        return ResponseEntity.ok(assessmentService.getCourseAssessments(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssessment(@PathVariable String id) {
        try {
            return ResponseEntity.ok(assessmentService.getAssessment(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitAssessment(@PathVariable String id, @RequestBody Map<Long, String> answers) {
        try {
            User learner = getCurrentUser();
            return ResponseEntity.ok(assessmentService.submitAssessment(learner, id, answers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
