package com.learnloop.backend.service;

import com.learnloop.backend.dto.FeedbackRequest;
import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.model.SessionFeedback;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.LearningSessionRepository;
import com.learnloop.backend.repository.SessionFeedbackRepository;
import com.learnloop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private SessionFeedbackRepository feedbackRepository;

    @Autowired
    private LearningSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    public SessionFeedback submitFeedback(User learner, FeedbackRequest request) {
        LearningSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getScheduler().getId().equals(learner.getId())) {
            throw new RuntimeException("Only the learner of the session can submit feedback");
        }

        User mentor = session.getPartner();

        SessionFeedback feedback = new SessionFeedback();
        feedback.setId(java.util.UUID.randomUUID().toString());
        feedback.setSessionId(session.getId());
        feedback.setLearner(learner);
        feedback.setMentor(mentor);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setTimestamp(LocalDateTime.now());

        SessionFeedback saved = feedbackRepository.save(feedback);

        // Recalculate Mentor's Average Rating
        List<SessionFeedback> mentorFeedbacks = feedbackRepository.findByMentorId(mentor.getId());
        double total = 0;
        for (SessionFeedback f : mentorFeedbacks) {
            total += f.getRating();
        }
        double avg = total / mentorFeedbacks.size();
        
        mentor.setAverageRating(Math.round(avg * 10.0) / 10.0);
        userRepository.save(mentor);

        return saved;
    }

    public List<SessionFeedback> getMentorFeedback(Long mentorId) {
        return feedbackRepository.findByMentorId(mentorId);
    }
}
