package com.learnloop.backend.repository;

import com.learnloop.backend.model.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningSessionRepository extends JpaRepository<LearningSession, String> {
    @Query("SELECT s FROM LearningSession s WHERE s.scheduler.id = :userId OR s.partner.id = :userId ORDER BY s.scheduledTime DESC")
    List<LearningSession> findAllByUserId(Long userId);
    List<LearningSession> findByCourseId(String courseId);
}
