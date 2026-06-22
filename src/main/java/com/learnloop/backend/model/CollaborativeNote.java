package com.learnloop.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaborative_notes")
public class CollaborativeNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── No-arg constructor ──
    public CollaborativeNote() {}

    // ── All-arg constructor ──
    public CollaborativeNote(Long id, User userA, User userB, String content, LocalDateTime updatedAt) {
        this.id = id;
        this.userA = userA;
        this.userB = userB;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public User getUserA() { return userA; }
    public User getUserB() { return userB; }
    public String getContent() { return content; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setUserA(User userA) { this.userA = userA; }
    public void setUserB(User userB) { this.userB = userB; }
    public void setContent(String content) { this.content = content; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User userA;
        private User userB;
        private String content;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userA(User userA) { this.userA = userA; return this; }
        public Builder userB(User userB) { this.userB = userB; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public CollaborativeNote build() {
            return new CollaborativeNote(id, userA, userB, content, updatedAt);
        }
    }
}
