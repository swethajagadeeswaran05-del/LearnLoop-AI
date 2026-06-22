package com.learnloop.backend.repository;

import com.learnloop.backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.sender.id = :userAId AND m.receiver.id = :userBId) OR " +
           "(m.sender.id = :userBId AND m.receiver.id = :userAId) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(Long userAId, Long userBId);
}
