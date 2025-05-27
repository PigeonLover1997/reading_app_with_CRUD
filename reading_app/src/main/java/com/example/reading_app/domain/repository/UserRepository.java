package com.example.reading_app.domain.repository;

import com.example.reading_app.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ユーザー名から検索するメソッド（Spring Data JPAが自動で実装）
    Optional<User> findByUsername(String username);

    // findByIdメソッドはJpaRepositoryが提供するので、
    // ここで定義しなくてもUserServiceで実装可能
}
