package com.learnloop.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'LEARNER'")
    private String role = "LEARNER";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<UserSkill> skills = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── No-arg constructor ──
    public User() {}

    // ── All-arg constructor ──
    public User(Long id, String email, String password, String fullName, String bio,
                String avatarUrl, String experienceLevel, Double averageRating, List<UserSkill> skills, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.experienceLevel = experienceLevel;
        this.averageRating = averageRating != null ? averageRating : 0.0;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.createdAt = createdAt;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getExperienceLevel() { return experienceLevel; }
    public Double getAverageRating() { return averageRating; }
    public List<UserSkill> getSkills() { return skills; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public void setSkills(List<UserSkill> skills) { this.skills = skills; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ── Role getter/setter ──
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String email;
        private String password;
        private String fullName;
        private String bio;
        private String avatarUrl;
        private String experienceLevel;
        private Double averageRating = 0.0;
        private List<UserSkill> skills = new ArrayList<>();
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public Builder experienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; return this; }
        public Builder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public Builder skills(List<UserSkill> skills) { this.skills = skills; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public User build() {
            return new User(id, email, password, fullName, bio, avatarUrl, experienceLevel, averageRating, skills, createdAt);
        }
    }
}
