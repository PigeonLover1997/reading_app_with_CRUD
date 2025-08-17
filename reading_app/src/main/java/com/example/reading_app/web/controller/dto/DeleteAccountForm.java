package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ユーザーによるアカウント削除フォームのDTOクラス。
 *
 * アカウント削除は、
 * ・「本当に本人かどうか」
 * ・「誤操作ではないか」
 * を確認するために、パスワードの再入力を求めるのが一般的。
 *
 * このクラスはそのパスワード入力を受け取るためのフォームデータ構造。
 */

 // ユーザーによるアカウント削除（無効フラグ立て）用
public class DeleteAccountForm {
    @NotBlank(message = "パスワードを入力してください")
    private String password;

    // getter/setter
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
