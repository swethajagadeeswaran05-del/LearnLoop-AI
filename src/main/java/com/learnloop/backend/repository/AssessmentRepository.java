package com.learnloop.backend.repository;

import com.learnloop.backend.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, String> {
    List<Assessment> findByUserId(Long userId);
    List<Assessment> findByCourseId(String courseId);
}
