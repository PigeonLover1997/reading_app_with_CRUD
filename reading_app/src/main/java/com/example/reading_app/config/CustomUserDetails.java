package com.example.reading_app.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.reading_app.domain.model.User;

/**
 * このクラスは、アプリケーション独自の User エンティティを
 * Spring Security が利用できる形（UserDetails）に変換するラッパーです。
 *
 * ログイン時、Spring Securityはこのクラスを通じて「ユーザー名・パスワード・権限」などの情報を取り扱います。
 */
public class CustomUserDetails implements UserDetails {

    private final User user; // アプリケーションのUserエンティティ（DB上のユーザー情報）

    // コンストラクタでUserエンティティを受け取って保持
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * ユーザーに付与された権限（ロール）を返す。
     * Spring Securityの仕様として、ロールには "ROLE_" プレフィックスをつける必要がある。
     * 例: ユーザーのロールが "ADMIN" の場合、"ROLE_ADMIN" という権限として扱う。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }

    /**
     * 認証時に使用されるパスワード情報を返す。
     * ここではDB上に保存されているハッシュ済みのパスワードを返す。
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * 認証時に使用されるユーザー名を返す。
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // UserDetails インターフェースに定義されている、アカウント状態チェック用の4メソッド
    // まだUserエンティティが該当フィールドを持っていないため仮でtrueを返す処理のみ
    // もし将来的に、一定期間ログインしなかったらアカウントを期限切れにしたい
    // 管理画面でユーザーをロック／アンロックできるようにしたい
    // メール認証が完了したユーザーだけ有効化したい
    // などの要件が出たら、users テーブルにADD COLUMN account_non_expired BOOLEAN DEFAULT TRUEなどのフラグを追加、
    // CustomUserDetails（このクラス）側で各メソッドがそのカラム値を返すように実装し直すことで対応可能
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }

    /**
     * アプリ内でユーザーのIDを参照したいときのためのユーティリティメソッド。
     * 例: 認証中のユーザーのIDを取得してDB操作する際などに使用。
     * 
     * ユーティリティメソッド：特定の機能を補助するための、小さな便利メソッドのこと
     */
    public Long getId() {
        return user.getId();
    }
}
