package com.learnloop.backend.controller;

import com.learnloop.backend.model.Assessment;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.AssessmentRepository;
import com.learnloop.backend.service.UserService;
import com.learnloop.backend.service.CourseCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseCompletionService courseCompletionService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping
    public List<Assessment> getUserAssessments() {
        User currentUser = getCurrentUser();
        return assessmentRepository.findByUserId(currentUser.getId());
    }

    @PostMapping
    public Assessment submitAssessment(@RequestBody Assessment assessment) {
        assessment.setId(UUID.randomUUID().toString());
        assessment.setUser(getCurrentUser());
        assessment.setTimestamp(LocalDateTime.now());
        Assessment saved = assessmentRepository.save(assessment);

        if (Boolean.TRUE.equals(saved.getPassed()) && saved.getCourse() != null) {
            try {
                courseCompletionService.checkAndGenerateCertificate(saved.getUser().getId(), saved.getCourse().getId());
            } catch (Exception e) {
                System.err.println("Failed to trigger course completion check on AI assessment: " + e.getMessage());
            }
        }

        return saved;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable String id) {
        return assessmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
