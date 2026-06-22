package com.learnloop.backend.service;

import com.learnloop.backend.model.ChatMessage;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.repository.LearningSessionRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LearningSessionRepository sessionRepository;

    public ChatMessage saveMessage(User sender, Long receiverId, String content, String sessionId, String fileUrl) {
        User receiver = userService.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));

        LearningSession session = null;
        if (sessionId != null) {
            session = sessionRepository.findById(sessionId).orElse(null);
        }

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .session(session)
                .content(content)
                .fileUrl(fileUrl)
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(User currentUser, Long partnerId) {
        return chatMessageRepository.findChatHistory(currentUser.getId(), partnerId);
    }
}
