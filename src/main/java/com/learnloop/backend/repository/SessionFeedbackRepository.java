package com.learnloop.backend.repository;

import com.learnloop.backend.model.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, String> {
    List<SessionFeedback> findBySessionId(String sessionId);
    List<SessionFeedback> findByMentorId(Long mentorId);
    List<SessionFeedback> findByLearnerId(Long learnerId);
}
