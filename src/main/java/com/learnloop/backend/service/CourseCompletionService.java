package com.learnloop.backend.service;

import com.learnloop.backend.model.*;
import com.learnloop.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseCompletionService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningSessionRepository sessionRepository;

    @Autowired
    private MentorAssessmentRepository mentorAssessmentRepository;

    @Autowired
    private MentorAssessmentSubmissionRepository mentorAssessmentSubmissionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @Autowired
    private SharedResourceRepository sharedResourceRepository;

    @Autowired
    private ResourceAccessRepository resourceAccessRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Tracks resource access for a learner.
     */
    public ResourceAccess trackResourceAccess(Long learnerId, Long resourceId) {
        Optional<ResourceAccess> existing = resourceAccessRepository.findByLearnerIdAndResourceId(learnerId, resourceId);
        if (existing.isPresent()) {
            return existing.get();
        }
        ResourceAccess access = new ResourceAccess(learnerId, resourceId);
        return resourceAccessRepository.save(access);
    }

    /**
     * Public method to verify a certificate's authenticity.
     */
    public Optional<Certificate> verifyCertificate(String certificateId) {
        Optional<Certificate> certificateOpt = certificateRepository.findById(certificateId);
        if (certificateOpt.isPresent()) {
            Certificate certificate = certificateOpt.get();
            certificate.setStatus("VERIFIED");
            certificateRepository.save(certificate);
            return Optional.of(certificate);
        }
        return Optional.empty();
    }

    /**
     * Evaluates course completion eligibility and automatically generates certificate if met.
     */
    public Optional<Certificate> checkAndGenerateCertificate(Long learnerId, String courseId) {
        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> new RuntimeException("Learner not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Determine certificate type based on course's session type
        String type = "COURSE_COMPLETION";
        if ("study_group".equalsIgnoreCase(course.getSessionType())) {
            type = "COLLABORATIVE_LEARNING";
        } else if ("ai_path".equalsIgnoreCase(course.getSessionType())) {
            type = "AI_LEARNING_PATH";
        }

        // 1. Prevent duplicates
        Optional<Certificate> existing = certificateRepository.findByLearnerIdAndCourseIdAndCertificateType(learnerId, courseId, type);
        if (existing.isPresent()) {
            return existing;
        }

        // 2. Fetch Sessions for the course/learner
        List<LearningSession> courseSessions = sessionRepository.findByCourseId(courseId).stream()
                .filter(s -> s.getScheduler().getId().equals(learnerId) || s.getPartner().getId().equals(learnerId))
                .collect(Collectors.toList());

        // Attendance Check: Need at least 1 session and >= 80% COMPLETED status
        if (courseSessions.isEmpty()) {
            return Optional.empty(); // Cannot complete course with zero sessions
        }
        long completedSessions = courseSessions.stream()
                .filter(s -> "COMPLETED".equalsIgnoreCase(s.getStatus()))
                .count();
        double attendanceRate = (double) completedSessions / courseSessions.size();
        if (attendanceRate < 0.8) {
            return Optional.empty(); // Fails 80% attendance
        }

        // 3. Assessments Check
        List<MentorAssessment> assessments = mentorAssessmentRepository.findByCourseId(courseId);
        if (assessments.isEmpty()) {
            return Optional.empty(); // No assessments defined
        }
        double totalAssessmentScore = 0.0;
        int passedAssessmentsCount = 0;
        for (MentorAssessment ma : assessments) {
            List<MentorAssessmentSubmission> submissions = mentorAssessmentSubmissionRepository
                    .findByAssessmentIdAndLearnerId(ma.getId(), learnerId);
            Optional<MentorAssessmentSubmission> highestPassing = submissions.stream()
                    .filter(sub -> sub.getScore() != null && sub.getScore() >= 60.0)
                    .max(Comparator.comparingDouble(MentorAssessmentSubmission::getScore));
            if (highestPassing.isEmpty()) {
                return Optional.empty(); // Missing or failed mandatory assessment
            }
            totalAssessmentScore += highestPassing.get().getScore();
            passedAssessmentsCount++;
        }

        // 4. Assignments/Tasks Check
        // Find all tasks associated with the sessions of this course
        List<String> sessionIds = courseSessions.stream().map(LearningSession::getId).collect(Collectors.toList());
        List<Task> tasks = new ArrayList<>();
        for (String sid : sessionIds) {
            tasks.addAll(taskRepository.findBySessionId(sid));
        }
        if (tasks.isEmpty()) {
            return Optional.empty(); // No tasks defined
        }


        for (Task task : tasks) {
            List<TaskSubmission> submissions = taskSubmissionRepository.findByTaskIdAndLearnerId(task.getId(), learnerId);
            boolean approved = submissions.stream().anyMatch(sub -> "APPROVED".equalsIgnoreCase(sub.getStatus()));
            if (!approved) {
                return Optional.empty(); // Missing or unapproved mandatory task
            }
        }

        // 5. Materials/Resources Access Check
        List<SharedResource> resources = new ArrayList<>();
        for (String sid : sessionIds) {
            resources.addAll(sharedResourceRepository.findBySessionId(sid));
        }
        if (resources.isEmpty()) {
            return Optional.empty(); // No resources defined
        }


        for (SharedResource res : resources) {
            Optional<ResourceAccess> access = resourceAccessRepository.findByLearnerIdAndResourceId(learnerId, res.getId());
            if (access.isEmpty()) {
                return Optional.empty(); // Required resource not accessed
            }
        }

        // All criteria satisfied! Generate Course Completion Certificate
        Certificate certificate = new Certificate();
        certificate.setId(UUID.randomUUID().toString());
        certificate.setLearner(learner);
        certificate.setMentor(course.getTeacher());
        certificate.setCourseId(courseId);
        certificate.setCourseName(course.getTitle());
        certificate.setIssueDate(LocalDateTime.now());
        certificate.setStatus("ISSUED");
        certificate.setCertificateType(type);

        // Compute average score from assessments
        double avgScore = passedAssessmentsCount > 0 ? (totalAssessmentScore / passedAssessmentsCount) : 100.0;
        certificate.setFinalScore(avgScore);

        // Verification QR & URLs
        certificate.setCertificateUrl("/api/certificates/verify/" + certificate.getId());
        certificate.setVerificationQr("LLAI-VERIFY-" + certificate.getId());

        // Digital Signatures
        certificate.setPlatformSignature("LearnLoop AI Authorized Signature");
        if (course.getTeacher() != null) {
            certificate.setMentorSignature(course.getTeacher().getFullName() + " (Mentor)");
        }

        return Optional.of(certificateRepository.save(certificate));
    }
}
