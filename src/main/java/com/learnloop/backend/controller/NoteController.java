package com.learnloop.backend.controller;

import com.learnloop.backend.model.CollaborativeNote;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.NoteService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping("/{partnerId}")
    public ResponseEntity<?> getNote(@PathVariable Long partnerId) {
        try {
            User currentUser = getCurrentUser();
            CollaborativeNote note = noteService.getNoteBetweenUsers(currentUser, partnerId);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{partnerId}")
    public ResponseEntity<?> updateNote(@PathVariable Long partnerId, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null) {
            return ResponseEntity.badRequest().body("content parameter is required");
        }

        try {
            User currentUser = getCurrentUser();
            CollaborativeNote updated = noteService.updateNoteBetweenUsers(currentUser, partnerId, content);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
