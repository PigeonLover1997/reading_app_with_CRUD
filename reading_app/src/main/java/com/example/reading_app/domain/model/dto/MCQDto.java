package com.example.reading_app.domain.model.dto;

import java.util.List;

public class MCQDto {
    private String question; // 質問文
    private List<String> options; // 選択肢リスト
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
