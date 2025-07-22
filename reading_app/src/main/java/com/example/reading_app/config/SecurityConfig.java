package com.example.reading_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity // Spring Security を有効にする
@Configuration // このクラスが設定用であることを示す
// Spring Securityの設定クラス
public class SecurityConfig {
    // ユーザー情報を取得するサービス（後述の CustomUserDetailsService クラス）を注入
    private final CustomUserDetailsService customUserDetailsService;

    // コンストラクタで依存関係（CustomUserDetailsService）を注入する
    public SecurityConfig(CustomUserDetailsService uds) {
        this.customUserDetailsService = uds;
    }

    /**
     * パスワードをハッシュ化するためのエンコーダーを提供する。
     * セキュリティ上、平文のまま保存せず、BCrypt という方式で暗号化して保存する。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * アプリケーションのセキュリティルール（どのURLに誰がアクセスできるかなど）を定義する。
     * ログイン・ログアウトの挙動もここで設定する。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider()) // ← ここで認証プロバイダにCustomUserDetailsServiceを登録（）
                // ↑先に認証プロバイダの登録は不要（configureGlobal でやるので大丈夫）
                // ページごとのアクセス権を設定
                .authorizeHttpRequests(auth -> auth
                        // 管理者のみアクセス可：ユーザー管理ページ
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 誰でもアクセス可：トップページ、問題作成後の出題ページ、解答後のフィードバックページ、ユーザー登録ページ、エラーページ、CSS、Javascript
                        .requestMatchers("/", "/generate", "/submitAnswers", "/register", "/register/**", "/error",
                                "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated() // 他は認証（ユーザーログイン）必須
                )
                // フォームログインの設定（ログインページと成功時の遷移先）
                .formLogin(form -> form
                        .loginPage("/login") // ログインページのURL
                        .defaultSuccessUrl("/user/home", true) // ログイン成功後のリダイレクト先
                        .permitAll() // ログイン画面は誰でもアクセス可能
                )
                // ログアウトの設定
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout") // ログアウト後にゲスト用トップページにリダイレクト ログアウトを示すパラメータ"?logout"を付与
                );
        // 最終的に SecurityFilterChain（セキュリティ設定のまとめ）を返す
        return http.build();
    }

    /**
     * カスタムの認証プロバイダー（ユーザー情報＋パスワード照合）を定義。
     * Spring Security にこの認証方式を使わせる。
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        // ユーザー情報取得に使うサービスをセット
        ap.setUserDetailsService(customUserDetailsService);
        // パスワードの照合に使うエンコーダー（BCrypt）をセット
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }
}
