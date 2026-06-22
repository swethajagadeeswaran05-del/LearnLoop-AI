package com.learnloop.backend.dto;

import java.time.LocalDateTime;

public class SessionRequest {
    private String title;
    private String description;
    private Long partnerId;
    private LocalDateTime scheduledTime;
    private String courseId;

    public SessionRequest() {}

    public SessionRequest(String title, String description, Long partnerId, LocalDateTime scheduledTime, String courseId) {
        this.title = title;
        this.description = description;
        this.partnerId = partnerId;
        this.scheduledTime = scheduledTime;
        this.courseId = courseId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getPartnerId() { return partnerId; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public String getCourseId() { return courseId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
}
