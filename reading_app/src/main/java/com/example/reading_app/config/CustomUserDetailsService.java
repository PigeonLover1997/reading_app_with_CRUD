package com.example.reading_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;

/**
 * このクラスは、Spring Security にユーザー情報を提供するサービスです。
 * UserDetailsService インターフェースを実装しており、
 * 「ユーザー名からユーザー情報を取得する」機能を担います。
 * 
 * Spring Security の認証処理中に自動的に呼び出されます。
 * 
 * サービスクラス：業務処理（ビジネスロジック）を担うクラス
 * UserDetailsService インターフェース：Spring Security における、
 *                                    ユーザー情報を取得するための標準インターフェース
 */
@Service

// Spring SecurityのUserDetailsServiceインターフェースを実装したクラス
public class CustomUserDetailsService implements UserDetailsService {
    // DB操作のための UserRepository を注入
    @Autowired
    private UserRepository userRepository;

    // Spring の DI（依存性注入）コンテナが起動時に UserRepository のインスタンスを渡して、このサービスクラスを生成する
    // loadUserByUsername(...) の内部でユーザー検索を行う際、userRepository を利用して 
    // DBからユーザーを取得できるようにするための初期化処理
    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    /**
     * 認証時に Spring Security がこのメソッドを呼び出す。
     * ここで「ユーザー名」に対応するユーザー情報をDBから取得し、
     * 認証のための UserDetails（CustomUserDetails）として返す。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ユーザー名でDBからユーザー情報を検索
        User user = userRepository.findByUsername(username)
                // 見つからない場合は UsernameNotFoundException をスロー
                // Spring Securityの認証フィルター（内部ロジック）が自動でこの例外をキャッチし、「ログイン失敗」として扱い、エラーメッセージの表示（th:if="${param.error}"）につなげる
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
        // ユーザーが無効状態の場合はログイン不可
        if (!user.getIsActive()) {
            throw new DisabledException("無効なアカウントです（削除済、停止されているなど）");
        }
        // 通常通りユーザーが見つかり、アカウントも有効なら、
        // CustomUserDetails にラップして返す
        // CustomUserDetails implements UserDetailsなので、メソッドの戻り値の型の定義は UserDetails で問題ない
        return new CustomUserDetails(user); // 標準のUserDetailsではなく、独自のCustomUserDetailsを返す
    }
}
