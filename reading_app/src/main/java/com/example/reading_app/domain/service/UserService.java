package com.example.reading_app.domain.service;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;
import com.example.reading_app.web.controller.dto.AdminUserEditForm;
import com.example.reading_app.web.controller.dto.UserAccountForm;
import com.example.reading_app.web.controller.dto.UserEditForm;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("findById(Long id)でUserが見つかりませんでした"));
    }

    public List<User> findAll() {
        // UserRepositoryに追加したID順の検索の方を使用
        return userRepository.findAllByOrderByIdAsc();
    }

    // ユーザー自身によるユーザー情報（問題の希望条件）編集
    @Transactional
    public void updateProfile(Long userId, UserEditForm form) {
        User u = findById(userId);
        u.setDifficulty(form.getDifficulty());
        u.setWordCount(form.getWordCount());
        u.setQuestionCount(form.getQuestionCount());
        u.setTopic(form.getTopic());
        userRepository.save(u);
    }

    // ユーザー自身によるユーザー情報（ユーザー名・パスワード）編集
@Transactional
public boolean updateAccount(Long userId, UserAccountForm form) {
    User user = findById(userId);

    // ユーザー名変更
    user.setUsername(form.getUsername());

    // 現在のパスワードが一致しているか検証
    if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPasswordHash())) {
        // パスワード不一致の場合
        return false;
    }

    // 新しいパスワードをセット
    user.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
    userRepository.save(user);
    return true;
    }

    // ユーザー自身によるアカウント削除（無効フラグを立てて論理削除）
    @Transactional
    public boolean deleteAccount(Long userId, String rawPassword) {
        User u = findById(userId);
        // パスワードチェック
        if (!passwordEncoder.matches(rawPassword, u.getPasswordHash())) {
            return false;
        }
        // 論理削除
        u.setIsActive(false);
        userRepository.save(u);
        return true;
    }

    // 管理者による全項目編集
    @Transactional
    public void updateUserByAdmin(Long userId, AdminUserEditForm form) {
        User u = findById(userId);
        // 管理者は全ての編集可能項目を更新
        u.setUsername(form.getUsername());
        // パスワード欄が空欄でなければ更新
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        u.setRole(form.getRole());
        u.setIsActive(form.getIsActive());
        u.setDifficulty(form.getDifficulty());
        u.setWordCount(form.getWordCount());
        u.setQuestionCount(form.getQuestionCount());
        u.setTopic(form.getTopic());
        userRepository.save(u);
    }

    // ユーザー削除（管理者による物理削除）
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
