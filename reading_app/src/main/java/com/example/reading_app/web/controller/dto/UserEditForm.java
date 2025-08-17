package com.example.reading_app.web.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ユーザー自身によるプロフィール編集フォームのDTO。
 * - HTMLフォームの入力値を一時的に保持する「入力用オブジェクト」
 * - DBのUserエンティティとは分けて管理するのが一般的
 */
public class UserEditForm {
    // ユーザー自身によるユーザー情報（問題の希望条件）の編集用DTOクラス

    /*
     * DTOを使うメリット
     * 安全性向上：エンティティ（User）を直接フォームに使うと、意図しない項目まで更新されるリスクがある
     * 責務分離：DTOは「Webフォームとのやり取り専用」にでき、ビジネスロジックやDB設計と切り離せる
     * バリデーション制御：DTO単体に対して制約ルールを持てるため、柔軟に入力制御ができる
     */

    // ユーザー名は編集不可。hidden入力などで送られる想定のため、バリデーションは省略
    private String username;

    // パスワードはこのフォームでは扱わない（別フォームにて実装）

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
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
