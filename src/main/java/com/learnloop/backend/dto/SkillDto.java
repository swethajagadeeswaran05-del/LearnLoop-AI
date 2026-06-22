package com.learnloop.backend.dto;

public class SkillDto {
    private String skillName;
    private boolean teach; // true = can teach, false = wants to learn
    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED

    public SkillDto() {}

    public SkillDto(String skillName, boolean teach, String proficiency) {
        this.skillName = skillName;
        this.teach = teach;
        this.proficiency = proficiency;
    }

    public String getSkillName() { return skillName; }
    public boolean isTeach() { return teach; }
    public String getProficiency() { return proficiency; }

    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setTeach(boolean teach) { this.teach = teach; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }
}
