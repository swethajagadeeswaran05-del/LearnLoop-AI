package com.learnloop.backend.repository;

import com.learnloop.backend.model.MentorAssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorAssessmentQuestionRepository extends JpaRepository<MentorAssessmentQuestion, Long> {
}
