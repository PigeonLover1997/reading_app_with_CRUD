package com.example.reading_app.domain.model;

import java.util.List;

public class MCQDto {
    private String question;
    private List<String> options;
    private String answerLabel;  // "A" or "B" ...
    // getter / setter
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
    public String getAnswerLabel() {
        return answerLabel;
    }
    public void setAnswerLabel(String answerLabel) {
        this.answerLabel = answerLabel;
    }
}
