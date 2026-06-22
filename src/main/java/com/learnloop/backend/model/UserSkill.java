package com.learnloop.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "user_skills")
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "is_teach", nullable = false)
    private boolean isTeach; // true = can teach, false = wants to learn

    @Column(nullable = false)
    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED

    // ── No-arg constructor ──
    public UserSkill() {}

    // ── All-arg constructor ──
    public UserSkill(Long id, User user, String skillName, boolean isTeach, String proficiency) {
        this.id = id;
        this.user = user;
        this.skillName = skillName;
        this.isTeach = isTeach;
        this.proficiency = proficiency;
    }

    // ── Getters ──
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getSkillName() { return skillName; }
    public boolean isTeach() { return isTeach; }
    public String getProficiency() { return proficiency; }

    // ── Setters ──
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setTeach(boolean isTeach) { this.isTeach = isTeach; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private String skillName;
        private boolean isTeach;
        private String proficiency;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder skillName(String skillName) { this.skillName = skillName; return this; }
        public Builder isTeach(boolean isTeach) { this.isTeach = isTeach; return this; }
        public Builder proficiency(String proficiency) { this.proficiency = proficiency; return this; }

        public UserSkill build() {
            return new UserSkill(id, user, skillName, isTeach, proficiency);
        }
    }
}
