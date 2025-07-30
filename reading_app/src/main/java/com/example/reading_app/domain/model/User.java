package com.example.reading_app.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity //「このクラスはDBのテーブルにマッピングされるよ」と宣言するアノテーション
// Spring Data JPAがこのクラスを「永続化エンティティ」として扱い、save()やfindById()などが使えるようになる

// テーブル名・カラム名はFlywayの対応SQLファイルと完全一致させる必要あり
// ここではクラス名はUserだが、テーブル名はusersとする
@Table(name = "users") // クラスに付与し、エンティティがどのテーブルに対応しているかを指定するアノテーション
// このアノテーションを省略すると、クラス名と同じテーブル名

public class User {
    // JPAで主キーを指定するアノテーション　自動採番とセットで使うことが多い
    @Id
    // 一意のIDを自動採番するアノテーション
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    // ユーザー名を保存するカラム
    // nullable = falseは「nullを許容しない」、unique = trueは「一意であることを保証する」、
    // length = 100は「最大文字数を100に制限する」という意味
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    // パスワードのハッシュ値を保存するカラム
    // フィールド名とカラム名を別にする場合name属性でカラム名を指定
    // Javaはキャメルケース、DBはスネークケースが一般的
    @Column(nullable = false, name = "password_hash", length = 255)
    private String passwordHash;

    // ユーザーのロールを示すカラム
    @Column(nullable = false, length = 20)
    private String role = "USER"; // "USER" or "ADMIN"。初期値USER

    // ユーザーの作成日時を保存するカラム
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // 初期値として現在時刻を設定

    //ユーザーの希望する難易度・語数・問題数・トピックを保存するカラムの追加に対応したフィールド
    //下記のカラムに対応するもの
    /*  ADD COLUMN difficulty VARCHAR(16),
    ADD COLUMN word_count INTEGER,
    ADD COLUMN question_count INTEGER,
    ADD COLUMN topic VARCHAR(1000); */  
    // 設定しない場合を考慮してnullable=trueを指定  
    @Column(nullable = true, length = 16)
    private String difficulty;
    @Column(nullable = true)
    private Integer wordCount;
    @Column(nullable = true)
    private Integer questionCount;
    @Column(nullable = true, length = 1000)
    private String topic;

    // ユーザーが有効かどうかを示すフラグ
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true; // 初期値はtrue（有効）


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

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic;}

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

}
