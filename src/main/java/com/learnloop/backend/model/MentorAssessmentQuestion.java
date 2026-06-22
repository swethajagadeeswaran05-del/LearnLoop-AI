package com.learnloop.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "mentor_assessment_questions")
public class MentorAssessmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    @JsonIgnore
    private MentorAssessment assessment;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson; // for MCQ

    @Column(name = "correct_answer")
    private String correctAnswer;

    public MentorAssessmentQuestion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MentorAssessment getAssessment() { return assessment; }
    public void setAssessment(MentorAssessment assessment) { this.assessment = assessment; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
