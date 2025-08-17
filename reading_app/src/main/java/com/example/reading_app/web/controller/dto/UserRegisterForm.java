package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録フォーム用DTO（データ転送オブジェクト）
 * Webフォームの入力内容を一時的に保持してコントローラに渡す役割を持つクラス。
 * 登録画面（/register）と対応。
 */
public class UserRegisterForm {
    // 登録時のみ必須。編集画面では含まない
    @NotBlank(message = "ユーザー名は必須です")
    private String username;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 4, message = "パスワードは4文字以上で入力してください（テスト用の仮のバリデーション）")
    private String password;

    // 以下は登録／編集共通
    @NotBlank(message = "難易度は必須です")
    private String difficulty;    // 難易度

    @NotNull(message = "語数は必須です")
    @Min(value = 1, message = "語数は1以上2000以下で設定してください")
    @Max(value = 2000, message = "語数は1以上2000以下で設定してください")
    private Integer wordCount;    // 希望語数

    @NotNull(message = "問題数は必須です")
    @Min(value = 1, message = "問題数は1以上10以下で設定してください")
    @Max(value = 10, message = "問題数は1以上10以下で設定してください")
    private Integer questionCount; // 選択式問題の数
    
    private String topic;         // トピック

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
