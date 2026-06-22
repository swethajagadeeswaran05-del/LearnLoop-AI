package com.learnloop.backend.controller;

import com.learnloop.backend.model.ChatMessage;
import com.learnloop.backend.model.User;
import com.learnloop.backend.service.ChatService;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping("/history/{partnerId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long partnerId) {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(chatService.getChatHistory(currentUser, partnerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/message")
    public ResponseEntity<?> saveMessage(@RequestBody Map<String, Object> body) {
        try {
            User currentUser = getCurrentUser();
            Long receiverId = ((Number) body.get("receiverId")).longValue();
            String content = (String) body.get("content");
            String sessionId = (String) body.get("sessionId");
            String fileUrl = (String) body.get("fileUrl");
            
            ChatMessage saved = chatService.saveMessage(currentUser, receiverId, content, sessionId, fileUrl);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @MessageMapping("/chat.sendMessage")
    public void sendWebSocketMessage(@Payload Map<String, Object> payload) {
        try {
            Long senderId = Long.valueOf(payload.get("senderId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String content = payload.get("content").toString();
            String sessionId = payload.containsKey("sessionId") && payload.get("sessionId") != null ? payload.get("sessionId").toString() : null;
            String fileUrl = payload.containsKey("fileUrl") && payload.get("fileUrl") != null ? payload.get("fileUrl").toString() : null;

            User sender = userService.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
            
            ChatMessage saved = chatService.saveMessage(sender, receiverId, content, sessionId, fileUrl);

            // Broadcast to the receiver's specific queue
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/messages",
                    saved
            );
        } catch (Exception e) {
            System.err.println("Error processing WS message: " + e.getMessage());
        }
    }
}
