package com.learnloop.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_assessment_submissions")
public class MentorAssessmentSubmission {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assessment_id", nullable = false)
    private MentorAssessment assessment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @Column(nullable = false)
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column
    private String status; // SUBMITTED, GRADED

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
        if (this.status == null) this.status = "SUBMITTED";
    }

    public MentorAssessmentSubmission() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public MentorAssessment getAssessment() { return assessment; }
    public void setAssessment(MentorAssessment assessment) { this.assessment = assessment; }
    public User getLearner() { return learner; }
    public void setLearner(User learner) { this.learner = learner; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
