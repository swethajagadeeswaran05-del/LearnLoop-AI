package com.learnloop.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_sessions")
public class LearningSession {

    @Id
    private String id;

    @Column(name = "course_id")
    private String courseId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduler_id", nullable = false)
    private User scheduler;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id", nullable = false)
    private User partner;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, REJECTED, COMPLETED

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "credits_paid")
    private Integer creditsPaid;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(name = "assessment_passed")
    private Boolean assessmentPassed;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "recording_url")
    private String recordingUrl;

    public LearningSession() {}

    public LearningSession(String id, String courseId, String title, String description, User scheduler, User partner,
                           LocalDateTime scheduledTime, String status, String paymentMethod, Integer creditsPaid, Double amountPaid, Boolean assessmentPassed, String meetingLink, String recordingUrl) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.scheduler = scheduler;
        this.partner = partner;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.creditsPaid = creditsPaid;
        this.amountPaid = amountPaid;
        this.assessmentPassed = assessmentPassed;
        this.meetingLink = meetingLink;
        this.recordingUrl = recordingUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getScheduler() { return scheduler; }
    public void setScheduler(User scheduler) { this.scheduler = scheduler; }

    public User getPartner() { return partner; }
    public void setPartner(User partner) { this.partner = partner; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Integer getCreditsPaid() { return creditsPaid; }
    public void setCreditsPaid(Integer creditsPaid) { this.creditsPaid = creditsPaid; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public Boolean getAssessmentPassed() { return assessmentPassed; }
    public void setAssessmentPassed(Boolean assessmentPassed) { this.assessmentPassed = assessmentPassed; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getRecordingUrl() { return recordingUrl; }
    public void setRecordingUrl(String recordingUrl) { this.recordingUrl = recordingUrl; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String courseId;
        private String title;
        private String description;
        private User scheduler;
        private User partner;
        private LocalDateTime scheduledTime;
        private String status;
        private String paymentMethod;
        private Integer creditsPaid;
        private Double amountPaid;
        private Boolean assessmentPassed;
        private String meetingLink;
        private String recordingUrl;

        public Builder id(String id) { this.id = id; return this; }
        public Builder courseId(String courseId) { this.courseId = courseId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder scheduler(User scheduler) { this.scheduler = scheduler; return this; }
        public Builder partner(User partner) { this.partner = partner; return this; }
        public Builder scheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder creditsPaid(Integer creditsPaid) { this.creditsPaid = creditsPaid; return this; }
        public Builder amountPaid(Double amountPaid) { this.amountPaid = amountPaid; return this; }
        public Builder assessmentPassed(Boolean assessmentPassed) { this.assessmentPassed = assessmentPassed; return this; }
        public Builder meetingLink(String meetingLink) { this.meetingLink = meetingLink; return this; }
        public Builder recordingUrl(String recordingUrl) { this.recordingUrl = recordingUrl; return this; }

        public LearningSession build() {
            return new LearningSession(id, courseId, title, description, scheduler, partner, scheduledTime, status, paymentMethod, creditsPaid, amountPaid, assessmentPassed, meetingLink, recordingUrl);
        }
    }
}
