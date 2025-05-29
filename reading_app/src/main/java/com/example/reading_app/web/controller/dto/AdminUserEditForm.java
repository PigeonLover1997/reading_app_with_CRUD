package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminUserEditForm {

    // 編集フォームでhiddenで持つ想定（DBの主キー、管理者でも編集不可）
    private Long id;

    @NotBlank(message = "ユーザー名は必須です")
    private String username;

    // パスワード：未入力の場合は変更しない、入力時はハッシュ化して保存
    @Size(min = 4, message = "パスワードは4文字以上で入力してください（テスト用バリデーション）", groups = PasswordRequired.class)
    private String password;

    @NotBlank(message = "権限は必須です")
    private String role; // "USER" or "ADMIN"

    @NotNull(message = "有効・無効を選択してください")
    private Boolean isActive;

    // ユーザー・管理者共通編集可能項目
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    // グループインターフェース
    public interface PasswordRequired {}
}
