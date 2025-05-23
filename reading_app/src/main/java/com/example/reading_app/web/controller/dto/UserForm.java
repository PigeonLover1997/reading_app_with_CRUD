package com.example.reading_app.web.controller.dto;

// ユーザー登録フォーム用DTO
public class UserForm {
    private String username;
    private String password;
    private String difficulty;    // 難易度
    private Integer wordCount;    // 希望語数
    private Integer questionCount; // 選択式問題の数
    private String topic;         // トピック（以上4項目を後から追加）

    // getter / setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
