package com.example.reading_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    private final CustomUserDetailsService customUserDetailsService;

    // customUserDetailsServiceをコンストラクタで Inject する
    public SecurityConfig(CustomUserDetailsService uds) {
        this.customUserDetailsService = uds;
    }

    // パスワードハッシュ化にBCryptを利用
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())   // ← ここで認証プロバイダにCustomUserDetailsServiceを登録（）
            // ↑先に認証プロバイダの登録は不要（configureGlobal でやるので大丈夫）
            // ページごとのアクセス権を設定
            .authorizeHttpRequests(auth -> auth
                // 誰でもアクセス可：トップページ、問題作成後の出題ページ、解答後のフィードバックページ、ユーザー登録ページ、CSS、Javascript
                .requestMatchers("/","/generate", "/submitAnswers","/register","/register/**", "/css/**", "/js/**").permitAll() 
                .anyRequest().authenticated() // 他は認証必須
            )
            // フォームログインの設定
            .formLogin(form -> form
                .loginPage("/login")      // ログインページのURL
                .defaultSuccessUrl("/user/home", true) // ここを追加
                .permitAll()              // 誰でもアクセス可
            )
            // ログアウトの設定
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")    
                // ログアウト後にゲスト用トップページにリダイレクト ログアウトを示すパラメータ"?logout"を付与
            );
        return http.build();
    }



    //認証プロバイダを登録
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(customUserDetailsService);
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }
}
