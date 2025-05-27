package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// ユーザー編集フォーム用DTO
public class UserEditForm {

    // ユーザー名は編集不可。hiddenで送る場合はバリデーション無しでもOK
    private String username;

    // パスワード項目なし

    @NotBlank(message = "難易度は必須です")
    private String difficulty;

    @NotNull(message = "語数は必須です")
    @Min(value = 1, message = "語数は1以上2000以下で設定してください")
    @Max(value = 2000, message = "語数は1以上2000以下で設定してください")
    private Integer wordCount;

    @NotNull(message = "問題数は必須です")
    @Min(value = 1, message = "問題数は1以上10以下で設定してください")
    @Max(value = 10, message = "問題数は1以上10以下で設定してください")
    private Integer questionCount;

    private String topic;

    // getter / setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
