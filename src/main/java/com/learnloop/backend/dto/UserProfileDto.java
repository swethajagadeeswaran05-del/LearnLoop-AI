package com.learnloop.backend.dto;

import java.util.List;

public class UserProfileDto {
    private String fullName;
    private String bio;
    private String avatarUrl;
    private String experienceLevel;
    private List<SkillDto> skills;

    public UserProfileDto() {}

    public UserProfileDto(String fullName, String bio, String avatarUrl,
                          String experienceLevel, List<SkillDto> skills) {
        this.fullName = fullName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.experienceLevel = experienceLevel;
        this.skills = skills;
    }

    public String getFullName() { return fullName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getExperienceLevel() { return experienceLevel; }
    public List<SkillDto> getSkills() { return skills; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setSkills(List<SkillDto> skills) { this.skills = skills; }
}
