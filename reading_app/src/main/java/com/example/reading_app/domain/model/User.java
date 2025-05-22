package com.example.reading_app.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity //「このクラスはDBのテーブルにマッピングされるよ」と宣言するアノテーション
// Spring Data JPAがこのクラスを「永続化エンティティ」として扱い、save()やfindById()などが使えるようになる

// テーブル名・カラム名はFlywayの対応SQLファイルと完全一致させる必要あり
// ここではクラス名はUserだが、テーブル名はusersとする
@Table(name = "users") // クラスに付与し、エンティティがどのテーブルに対応しているかを指定するアノテーション
// このアノテーションを省略すると、クラス名と同じテーブル名

public class User {
    @Id
    // 一意のIDを自動採番するアノテーション
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    // フィールド名とカラム名を別にする場合name属性でカラム名を指定
    // Javaはキャメルケース、DBはスネークケースが一般的
    @Column(nullable = false, name = "password_hash", length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role = "USER"; // "USER" or "ADMIN"。初期値USER

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // 初期値として現在時刻を設定

    // --- getter/setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
