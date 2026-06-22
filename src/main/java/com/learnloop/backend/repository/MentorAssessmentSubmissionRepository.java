package com.learnloop.backend.repository;

import com.learnloop.backend.model.MentorAssessmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorAssessmentSubmissionRepository extends JpaRepository<MentorAssessmentSubmission, String> {
    List<MentorAssessmentSubmission> findByAssessmentId(String assessmentId);
    List<MentorAssessmentSubmission> findByLearnerId(Long learnerId);
    List<MentorAssessmentSubmission> findByAssessmentIdAndLearnerId(String assessmentId, Long learnerId);
}
