package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.NotBlank;

// ユーザーによるアカウント削除（無効フラグ立て）用
public class DeleteAccountForm {
    @NotBlank(message = "パスワードを入力してください")
    private String password;

    // getter/setter
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
