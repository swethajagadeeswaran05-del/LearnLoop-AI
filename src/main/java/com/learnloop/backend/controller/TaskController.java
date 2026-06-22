package com.learnloop.backend.controller;

import com.learnloop.backend.model.Task;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.TaskService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            User mentor = getCurrentUser();
            return ResponseEntity.ok(taskService.createTask(mentor, task));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionTasks(@PathVariable String sessionId) {
        return ResponseEntity.ok(taskService.getSessionTasks(sessionId));
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<?> submitTask(@PathVariable String taskId, @RequestBody Map<String, String> body) {
        try {
            User learner = getCurrentUser();
            String fileUrl = body.get("fileUrl");
            return ResponseEntity.ok(taskService.submitTask(learner, taskId, fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/submissions/{submissionId}/review")
    public ResponseEntity<?> reviewSubmission(@PathVariable String submissionId, @RequestBody Map<String, String> body) {
        try {
            User mentor = getCurrentUser();
            String status = body.get("status");
            String feedback = body.get("feedback");
            return ResponseEntity.ok(taskService.reviewSubmission(mentor, submissionId, status, feedback));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{taskId}/submissions")
    public ResponseEntity<?> getTaskSubmissions(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getSubmissionsForTask(taskId));
    }
}
