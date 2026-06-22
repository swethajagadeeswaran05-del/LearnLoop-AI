package com.learnloop.backend.controller;

import com.learnloop.backend.dto.FeedbackRequest;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.FeedbackService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(feedbackService.submitFeedback(currentUser, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getMentorFeedback(@PathVariable Long mentorId) {
        try {
            return ResponseEntity.ok(feedbackService.getMentorFeedback(mentorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
