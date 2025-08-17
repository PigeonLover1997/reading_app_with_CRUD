package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ユーザー自身がアカウント設定画面で
 * ・ユーザー名の変更
 * ・パスワードの変更
 * を行うためのフォームクラス（DTO）。
 *
 * - HTMLフォームで入力された値を受け取り、
 * - コントローラに渡してバリデーションやサービス処理に使う。
 */
public class UserAccountForm {
// ユーザー自身によるユーザー情報（ユーザー名・パスワード）の編集用DTO

    @NotBlank(message = "ユーザー名は必須です")
    private String username;

    @NotBlank(message = "現在のパスワードは必須です")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードは必須です")
    @Size(min = 4, message = "新しいパスワードは4文字以上で入力してください")
    private String newPassword;

    // getter/setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
