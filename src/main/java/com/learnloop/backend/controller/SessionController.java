package com.learnloop.backend.controller;

import com.learnloop.backend.dto.SessionRequest;
import com.learnloop.backend.dto.SharedResourceDto;
import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.SessionService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @PostMapping
    public ResponseEntity<?> bookSession(@RequestBody SessionRequest request) {
        try {
            User currentUser = getCurrentUser();
            LearningSession session = sessionService.bookSession(currentUser, request);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSessions() {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(sessionService.getSessions(currentUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateSessionStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body("status parameter is required");
        }

        try {
            User currentUser = getCurrentUser();
            LearningSession updated = sessionService.updateSessionStatus(id, status, currentUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startLiveSession(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            LearningSession updated = sessionService.startLiveSession(id, currentUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endLiveSession(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            LearningSession updated = sessionService.endLiveSession(id, currentUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resources")
    public ResponseEntity<?> shareResource(@RequestBody SharedResourceDto dto) {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(sessionService.shareResource(currentUser, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/resources")
    public ResponseEntity<?> getResources(@PathVariable String id) {
        try {
            return ResponseEntity.ok(sessionService.getResourcesForSession(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
