package com.learnloop.backend.repository;

import com.learnloop.backend.model.MentorAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorAssessmentRepository extends JpaRepository<MentorAssessment, String> {
    List<MentorAssessment> findByMentorId(Long mentorId);
    List<MentorAssessment> findByCourseId(String courseId);
}
