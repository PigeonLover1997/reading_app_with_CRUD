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
// 0722ここまでコメント付加済み
// Spring SecurityのUserDetailsServiceインターフェースを実装したクラス
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    // Spring の DI（依存性注入）コンテナが起動時に UserRepository のインスタンスを渡して、このサービスクラスを生成する
    // loadUserByUsername(...) の内部でユーザー検索を行う際、userRepository を利用して DB
    // からユーザーを取得できるようにするための初期化処理
    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                // Spring Security
                // の認証フィルター（内部ロジック）が自動でこの例外をキャッチし、「ログイン失敗」として扱い、エラーメッセージの表示（th:if="${param.error}"）につなげる
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
        // ユーザーが無効状態の場合        
        if (!user.getIsActive()) {
            throw new DisabledException("無効なアカウントです（削除済、停止されているなど）");
        }
        return new CustomUserDetails(user); // 標準のUserDetailsではなく、独自のCustomUserDetailsを返す
    }
}
