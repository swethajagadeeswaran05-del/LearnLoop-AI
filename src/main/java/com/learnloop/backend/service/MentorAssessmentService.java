package com.learnloop.backend.service;

import com.learnloop.backend.model.MentorAssessment;
import com.learnloop.backend.model.MentorAssessmentQuestion;
import com.learnloop.backend.model.MentorAssessmentSubmission;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.MentorAssessmentRepository;
import com.learnloop.backend.repository.MentorAssessmentQuestionRepository;
import com.learnloop.backend.repository.MentorAssessmentSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MentorAssessmentService {

    @Autowired
    private MentorAssessmentRepository assessmentRepository;

    @Autowired
    private MentorAssessmentQuestionRepository questionRepository;

    @Autowired
    private MentorAssessmentSubmissionRepository submissionRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CourseCompletionService courseCompletionService;

    public MentorAssessment createAssessment(User mentor, MentorAssessment assessment) {
        assessment.setId(UUID.randomUUID().toString());
        assessment.setMentor(mentor);
        
        MentorAssessment savedAssessment = assessmentRepository.save(assessment);
        
        if (assessment.getQuestions() != null) {
            for (MentorAssessmentQuestion q : assessment.getQuestions()) {
                q.setAssessment(savedAssessment);
                questionRepository.save(q);
            }
        }
        
        return savedAssessment;
    }

    public List<MentorAssessment> getCourseAssessments(String courseId) {
        return assessmentRepository.findByCourseId(courseId);
    }

    public MentorAssessment getAssessment(String id) {
        return assessmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assessment not found"));
    }

    public MentorAssessmentSubmission submitAssessment(User learner, String assessmentId, Map<Long, String> answers) {
        MentorAssessment assessment = getAssessment(assessmentId);
        
        int correct = 0;
        for (MentorAssessmentQuestion q : assessment.getQuestions()) {
            if (q.getCorrectAnswer().equalsIgnoreCase(answers.get(q.getId()))) {
                correct++;
            }
        }
        
        double score = (double) correct / assessment.getQuestions().size() * 100;
        
        MentorAssessmentSubmission submission = new MentorAssessmentSubmission();
        submission.setId(UUID.randomUUID().toString());
        submission.setAssessment(assessment);
        submission.setLearner(learner);
        submission.setScore(score);
        submission.setStatus("GRADED");
        
        MentorAssessmentSubmission saved = submissionRepository.save(submission);
        
        if (score >= 60.0) {
            certificateService.generateCertificate(learner, assessment.getMentor(), assessment.getCourseId());
            try {
                courseCompletionService.checkAndGenerateCertificate(learner.getId(), assessment.getCourseId());
            } catch (Exception e) {
                System.err.println("Failed to trigger course completion check on assessment submit: " + e.getMessage());
            }
        }
        
        return saved;
    }
}
