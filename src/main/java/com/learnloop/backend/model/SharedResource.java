package com.learnloop.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_resources")
public class SharedResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(nullable = false)
    private String title;

    @Column(name = "type")
    private String type;

    @Column(name = "resource_url", columnDefinition = "TEXT")
    private String resourceUrl;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── No-arg constructor ──
    public SharedResource() {}

    // ── All-arg constructor ──
    public SharedResource(Long id, LearningSession session, User uploader, String title, String type,
                          String resourceUrl, String filePath, LocalDateTime createdAt) {
        this.id = id;
        this.session = session;
        this.uploader = uploader;
        this.title = title;
        this.type = type;
        this.resourceUrl = resourceUrl;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public LearningSession getSession() { return session; }
    public User getUploader() { return uploader; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getResourceUrl() { return resourceUrl; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setSession(LearningSession session) { this.session = session; }
    public void setUploader(User uploader) { this.uploader = uploader; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LearningSession session;
        private User uploader;
        private String title;
        private String type;
        private String resourceUrl;
        private String filePath;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder session(LearningSession session) { this.session = session; return this; }
        public Builder uploader(User uploader) { this.uploader = uploader; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder resourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SharedResource build() {
            return new SharedResource(id, session, uploader, title, type, resourceUrl, filePath, createdAt);
        }
    }
}
