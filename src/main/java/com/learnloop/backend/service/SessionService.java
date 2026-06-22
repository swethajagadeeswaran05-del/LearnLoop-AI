package com.learnloop.backend.service;

import com.learnloop.backend.dto.SessionRequest;
import com.learnloop.backend.dto.SharedResourceDto;
import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.model.SharedResource;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.LearningSessionRepository;
import com.learnloop.backend.repository.SharedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private LearningSessionRepository sessionRepository;

    @Autowired
    private SharedResourceRepository resourceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseCompletionService courseCompletionService;

    public LearningSession bookSession(User scheduler, SessionRequest request) {
        User partner = userService.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Partner user not found"));

        LearningSession session = LearningSession.builder()
                .id(java.util.UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduler(scheduler)
                .partner(partner)
                .scheduledTime(request.getScheduledTime())
                .status("PENDING")
                .courseId(request.getCourseId())
                .build();

        return sessionRepository.save(session);
    }

    public List<LearningSession> getSessions(User user) {
        return sessionRepository.findAllByUserId(user.getId());
    }

    public LearningSession updateSessionStatus(String sessionId, String status, User currentUser) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Only partner can accept or reject PENDING sessions
        if ("ACCEPTED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
            if (!session.getPartner().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Unauthorized: Only the partner can accept/reject a session invitation.");
            }
        }

        session.setStatus(status.toUpperCase());
        LearningSession saved = sessionRepository.save(session);

        if ("COMPLETED".equalsIgnoreCase(status) && saved.getCourseId() != null) {
            try {
                courseCompletionService.checkAndGenerateCertificate(saved.getScheduler().getId(), saved.getCourseId());
            } catch (Exception e) {
                System.err.println("Failed to check course completion on status update: " + e.getMessage());
            }
        }

        return saved;
    }

    public LearningSession startLiveSession(String sessionId, User currentUser) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getStatus().equalsIgnoreCase("ACCEPTED")) {
            throw new RuntimeException("Only ACCEPTED sessions can be started.");
        }

        session.setStatus("LIVE");
        // Generate Jitsi Meeting Link
        session.setMeetingLink("https://meet.jit.si/LearnLoop_Live_" + sessionId);
        return sessionRepository.save(session);
    }

    public LearningSession endLiveSession(String sessionId, User currentUser) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getStatus().equalsIgnoreCase("LIVE")) {
            throw new RuntimeException("Only LIVE sessions can be ended.");
        }

        session.setStatus("COMPLETED");
        // Auto-generate a mock recording link
        session.setRecordingUrl("/uploads/recording_" + sessionId + ".mp4");
        LearningSession saved = sessionRepository.save(session);

        if (saved.getCourseId() != null) {
            try {
                courseCompletionService.checkAndGenerateCertificate(saved.getScheduler().getId(), saved.getCourseId());
            } catch (Exception e) {
                System.err.println("Failed to check course completion on live end: " + e.getMessage());
            }
        }

        return saved;
    }

    public SharedResource shareResource(User uploader, SharedResourceDto dto) {
        LearningSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SharedResource resource = SharedResource.builder()
                .session(session)
                .uploader(uploader)
                .title(dto.getTitle())
                .resourceUrl(dto.getResourceUrl())
                .filePath(dto.getFilePath())
                .build();

        return resourceRepository.save(resource);
    }

    public List<SharedResource> getResourcesForSession(String sessionId) {
        return resourceRepository.findBySessionId(sessionId);
    }
}
