package com.example.reading_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ページごとのアクセス権を設定
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/css/**", "/js/**").permitAll() // 誰でもアクセス可
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

    // パスワードハッシュ化にBCryptを利用
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
