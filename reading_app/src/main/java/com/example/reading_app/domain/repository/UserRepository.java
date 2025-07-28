package com.example.reading_app.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reading_app.domain.model.User;

/**
 * User エンティティに対する DB 操作を行うリポジトリインターフェース。
 * Spring Data JPA によって、自動的に中身（実装）が作られる。
 * 
 * JpaRepository<User, Long> を継承することで、以下のような基本的なDB操作が使える：
 * - findAll()：全件取得
 * - findById(id)：IDで1件取得
 * - save(entity)：保存（insert or update）
 * - delete(entity)：削除
 */

// Spring Data JPAは、自動実装すべきメソッドを、
// @EnableJpaRepositories（@SpringBootApplication内の@EnableAutoConfigurationの中に含まれる）
// で自動的に検出している
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * ユーザー名でユーザーを検索するメソッド。
     * メソッド名に規則があり「findBy◯◯」と書くと、自動でSQLが組み立てられる。
     * 
     * - 戻り値は Optional<T>：nullチェックを安全に行うために使う型。
     * （＝User型の値があるかもしれないし、ないかもしれない、に対応するため）
     * - 例： userRepository.findByUsername("taro") でログイン用ユーザー情報取得に使われる。
     */
    Optional<User> findByUsername(String username);

    // findByIdメソッドはJpaRepositoryが提供するので、
    // ここで定義しなくてもUserServiceで実装可能

    // findAll()も、JpaRepositoryが自動で提供するのでUserServiceでそのまま使用可能

     /**
     * ID順にユーザー全件を取得するメソッド。
     * 「findAllByOrderByIdAsc」という命名規則で、自動的に「ORDER BY id ASC」が付いたSQLが生成される。
     * 例：管理画面でユーザー一覧をID順に表示したいときなどに使える。
     */
    List<User> findAllByOrderByIdAsc();
}
