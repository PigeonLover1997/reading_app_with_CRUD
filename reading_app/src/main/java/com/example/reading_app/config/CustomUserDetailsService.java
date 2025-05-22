package com.example.reading_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;

@Service
// Spring SecurityのUserDetailsServiceインターフェースを実装したクラス
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            //Spring Security の認証フィルター（内部ロジック）が自動でこの例外をキャッチし、「ログイン失敗」として扱い、エラーメッセージの表示（th:if="${param.error}"）につなげる
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .roles(user.getRole())
            .build();
    }
}
