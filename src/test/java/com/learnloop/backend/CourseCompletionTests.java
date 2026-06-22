package com.learnloop.backend;

import com.learnloop.backend.model.*;
import com.learnloop.backend.repository.*;
import com.learnloop.backend.service.CourseCompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
public class CourseCompletionTests {

    @Autowired
    private CourseCompletionService completionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LearningSessionRepository sessionRepository;

    @Autowired
    private MentorAssessmentRepository assessmentRepository;

    @Autowired
    private MentorAssessmentSubmissionRepository submissionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @Autowired
    private SharedResourceRepository resourceRepository;

    @Autowired
    private ResourceAccessRepository resourceAccessRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    private User learner;
    private User mentor;
    private Course course;

    @BeforeEach
    public void setup() {
        // Find existing database users loaded from schema/data sql
        learner = userRepository.findByEmail("swetha.j@gmail.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("swetha.j@gmail.com");
            u.setFullName("Swetha J");
            u.setPassword("encodedPassword");
            u.setRole("learner");
            return userRepository.save(u);
        });

        mentor = userRepository.findByEmail("aravind.kumar@gmail.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("aravind.kumar@gmail.com");
            u.setFullName("Aravind Kumar");
            u.setPassword("encodedPassword");
            u.setRole("mentor");
            return userRepository.save(u);
        });

        course = new Course();
        course.setId(UUID.randomUUID().toString());
        course.setTitle("Advanced Systems Design");
        course.setTeacher(mentor);
        course.setCreatedAt(LocalDateTime.now());
        course.setSessionType("mentor_led");
        course = courseRepository.save(course);
    }

    @Test
    public void testCourseCompletionVerificationAndEligibility() {
        // 1. Initial State: No sessions scheduled
        Optional<Certificate> initialCert = completionService.checkAndGenerateCertificate(learner.getId(), course.getId());
        assertTrue(initialCert.isEmpty(), "Should not generate certificate with 0 sessions");

        // 2. Schedule 5 sessions.
        // Session 1 to 4: COMPLETED
        // Session 5: ACCEPTED (attendance is 4/5 = 80%)
        List<LearningSession> sessions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            LearningSession s = new LearningSession();
            s.setId("sess-" + i + "-" + course.getId());
            s.setCourseId(course.getId());
            s.setTitle("Topic " + i);
            s.setScheduler(learner);
            s.setPartner(mentor);
            s.setScheduledTime(LocalDateTime.now().plusDays(i));
            s.setStatus(i <= 4 ? "COMPLETED" : "ACCEPTED");
            sessions.add(sessionRepository.save(s));
        }

        // Fails: Still no assessments, tasks, or resources configured or completed
        Optional<Certificate> noAssessmentsCert = completionService.checkAndGenerateCertificate(learner.getId(), course.getId());
        assertTrue(noAssessmentsCert.isEmpty(), "Should fail completion: no assessments/tasks completed");

        // Add 1 assessment for the course
        MentorAssessment assessment = new MentorAssessment();
        assessment.setId("assess-" + course.getId());
        assessment.setCourseId(course.getId());
        assessment.setMentor(mentor);
        assessment.setTitle("Design Patterns Quiz");
        assessment.setType("MCQ");
        assessment = assessmentRepository.save(assessment);

        // Add 1 task for Session 1
        Task task = new Task();
        task.setId("task-" + course.getId());
        task.setSessionId(sessions.get(0).getId());
        task.setMentor(mentor);
        task.setTitle("Design Assignment");
        task = taskRepository.save(task);

        // Add 1 resource/material for Session 2
        SharedResource resource = new SharedResource();
        resource.setSession(sessions.get(1));
        resource.setUploader(mentor);
        resource.setTitle("Architecture Slides");
        resource = resourceRepository.save(resource);

        // 3. Meet the assessment requirement (Submit & Pass: 75%)
        MentorAssessmentSubmission sub = new MentorAssessmentSubmission();
        sub.setId(UUID.randomUUID().toString());
        sub.setAssessment(assessment);
        sub.setLearner(learner);
        sub.setScore(75.0);
        sub.setStatus("GRADED");
        submissionRepository.save(sub);

        // 4. Meet the task requirement (Submit & Approve)
        TaskSubmission taskSub = new TaskSubmission();
        taskSub.setId(UUID.randomUUID().toString());
        taskSub.setTask(task);
        taskSub.setLearner(learner);
        taskSub.setStatus("APPROVED");
        taskSubmissionRepository.save(taskSub);

        // 5. Track resource access
        completionService.trackResourceAccess(learner.getId(), resource.getId());

        // 6. Check again -> Should generate certificate successfully (80% attendance, passed assessment, approved task, accessed resource)
        Optional<Certificate> successCert = completionService.checkAndGenerateCertificate(learner.getId(), course.getId());
        assertTrue(successCert.isPresent(), "Should successfully generate certificate");
        Certificate cert = successCert.get();
        assertEquals(course.getId(), cert.getCourseId());
        assertEquals("COURSE_COMPLETION", cert.getCertificateType());
        assertEquals(75.0, cert.getFinalScore());
        assertEquals("ISSUED", cert.getStatus());

        // 7. Verify Duplicate Prevention
        Optional<Certificate> duplicateCert = completionService.checkAndGenerateCertificate(learner.getId(), course.getId());
        assertTrue(duplicateCert.isPresent());
        assertEquals(cert.getId(), duplicateCert.get().getId(), "Should return existing certificate and avoid duplicate");

        // 8. Public verification check
        Optional<Certificate> verified = completionService.verifyCertificate(cert.getId());
        assertTrue(verified.isPresent());
        assertEquals("VERIFIED", verified.get().getStatus());
    }

    @Test
    public void testAttendanceBoundaryChecks() {
        // Schedule 5 sessions.
        // 3 COMPLETED, 2 ACCEPTED -> 3/5 = 60% (Fails 80% boundary)
        for (int i = 1; i <= 5; i++) {
            LearningSession s = new LearningSession();
            s.setId("sess-boundary-" + i + "-" + course.getId());
            s.setCourseId(course.getId());
            s.setTitle("Topic " + i);
            s.setScheduler(learner);
            s.setPartner(mentor);
            s.setScheduledTime(LocalDateTime.now().plusDays(i));
            s.setStatus(i <= 3 ? "COMPLETED" : "ACCEPTED");
            sessionRepository.save(s);
        }

        // Attendance check fails
        Optional<Certificate> boundaryCert = completionService.checkAndGenerateCertificate(learner.getId(), course.getId());
        assertTrue(boundaryCert.isEmpty(), "Attendance of 60% should fail completion check");
    }
}
