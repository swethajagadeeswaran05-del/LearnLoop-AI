package com.learnloop.backend.service;

import com.learnloop.backend.model.Task;
import com.learnloop.backend.model.TaskSubmission;
import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.TaskRepository;
import com.learnloop.backend.repository.TaskSubmissionRepository;
import com.learnloop.backend.repository.LearningSessionRepository;
import com.learnloop.backend.service.CourseCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskSubmissionRepository submissionRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseCompletionService courseCompletionService;

    @Autowired
    private LearningSessionRepository sessionRepository;

    public Task createTask(User mentor, Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setMentor(mentor);
        return taskRepository.save(task);
    }

    public List<Task> getMentorTasks(Long mentorId) {
        return taskRepository.findByMentorId(mentorId);
    }

    public List<Task> getSessionTasks(String sessionId) {
        return taskRepository.findBySessionId(sessionId);
    }

    public TaskSubmission submitTask(User learner, String taskId, String fileUrl) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        
        TaskSubmission submission = new TaskSubmission();
        submission.setId(UUID.randomUUID().toString());
        submission.setTask(task);
        submission.setLearner(learner);
        submission.setSubmissionFileUrl(fileUrl);
        
        return submissionRepository.save(submission);
    }

    public TaskSubmission reviewSubmission(User mentor, String submissionId, String status, String feedback) {
        TaskSubmission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Submission not found"));
        
        if (!submission.getTask().getMentor().getId().equals(mentor.getId())) {
            throw new RuntimeException("Only the mentor who created the task can review it");
        }

        submission.setStatus(status.toUpperCase());
        submission.setFeedback(feedback);

        TaskSubmission saved = submissionRepository.save(submission);

        if ("APPROVED".equalsIgnoreCase(status)) {
            certificateService.generateCertificate(submission.getLearner(), mentor, submission.getTask().getSessionId());
            try {
                LearningSession session = sessionRepository.findById(submission.getTask().getSessionId()).orElse(null);
                if (session != null && session.getCourseId() != null) {
                    courseCompletionService.checkAndGenerateCertificate(submission.getLearner().getId(), session.getCourseId());
                }
            } catch (Exception e) {
                System.err.println("Failed to trigger course completion check on task approval: " + e.getMessage());
            }
        }

        return saved;
    }

    public List<TaskSubmission> getSubmissionsForTask(String taskId) {
        return submissionRepository.findByTaskId(taskId);
    }
}
