package com.learnloop.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private LearningSession session;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    // ── No-arg constructor ──
    public ChatMessage() {}

    // ── All-arg constructor ──
    public ChatMessage(Long id, LearningSession session, User sender, User receiver, String content, String fileUrl, Boolean isRead, LocalDateTime timestamp) {
        this.id = id;
        this.session = session;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.fileUrl = fileUrl;
        this.isRead = isRead != null ? isRead : false;
        this.timestamp = timestamp;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public LearningSession getSession() { return session; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getContent() { return content; }
    public String getFileUrl() { return fileUrl; }
    public Boolean getIsRead() { return isRead; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setSession(LearningSession session) { this.session = session; }
    public void setSender(User sender) { this.sender = sender; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public void setContent(String content) { this.content = content; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LearningSession session;
        private User sender;
        private User receiver;
        private String content;
        private String fileUrl;
        private Boolean isRead = false;
        private LocalDateTime timestamp;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder session(LearningSession session) { this.session = session; return this; }
        public Builder sender(User sender) { this.sender = sender; return this; }
        public Builder receiver(User receiver) { this.receiver = receiver; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public Builder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ChatMessage build() {
            return new ChatMessage(id, session, sender, receiver, content, fileUrl, isRead, timestamp);
        }
    }
}
