package com.example.reading_app.web.controller.dto;

// ユーザー登録フォーム用DTO
public class UserForm {
    private String username;
    private String password;

    // getter / setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
