package com.learnloop.backend.dto;

import com.learnloop.backend.model.User;

import java.util.List;

public class MatchResponse {
    private User user;
    private double score;
    private List<String> skillGaps;

    public MatchResponse() {}

    public MatchResponse(User user, double score, List<String> skillGaps) {
        this.user = user;
        this.score = score;
        this.skillGaps = skillGaps;
    }

    public User getUser() { return user; }
    public double getScore() { return score; }
    public List<String> getSkillGaps() { return skillGaps; }

    public void setUser(User user) { this.user = user; }
    public void setScore(double score) { this.score = score; }
    public void setSkillGaps(List<String> skillGaps) { this.skillGaps = skillGaps; }
}
