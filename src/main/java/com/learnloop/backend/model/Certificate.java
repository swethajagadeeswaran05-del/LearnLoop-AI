package com.learnloop.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id", nullable = true) // Allow null for AI or self-paced courses
    private User mentor;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "verification_qr", columnDefinition = "TEXT")
    private String verificationQr;

    @Column(name = "status")
    private String status; // ISSUED, VERIFIED

    @Column(name = "certificate_type")
    private String certificateType; // COURSE_COMPLETION, COLLABORATIVE_LEARNING, AI_LEARNING_PATH, TASK_COMPLETION

    @Column(name = "mentor_signature")
    private String mentorSignature;

    @Column(name = "platform_signature")
    private String platformSignature;

    @PrePersist
    protected void onCreate() {
        this.issueDate = LocalDateTime.now();
        if (this.status == null) this.status = "ISSUED";
    }

    public Certificate() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getLearner() { return learner; }
    public void setLearner(User learner) { this.learner = learner; }
    public User getMentor() { return mentor; }
    public void setMentor(User mentor) { this.mentor = mentor; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }
    public String getVerificationQr() { return verificationQr; }
    public void setVerificationQr(String verificationQr) { this.verificationQr = verificationQr; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    public String getMentorSignature() { return mentorSignature; }
    public void setMentorSignature(String mentorSignature) { this.mentorSignature = mentorSignature; }
    public String getPlatformSignature() { return platformSignature; }
    public void setPlatformSignature(String platformSignature) { this.platformSignature = platformSignature; }
}
