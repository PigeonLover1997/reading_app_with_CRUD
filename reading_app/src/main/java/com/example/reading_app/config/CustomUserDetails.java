package com.example.reading_app.config;

import com.example.reading_app.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /** User.role → ROLE_xxx に変換 
     * Spring Security では、ロールを扱うときに ROLE_ という接頭辞を付ける
     * たとえば user.getRole() が "ADMIN" なら権限として "ROLE_ADMIN" が登録される
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

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

    /** コントローラ内でID取得に使えるユーティリティ */
    public Long getId() {
        return user.getId();
    }
}
