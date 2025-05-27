package com.example.reading_app.domain.service;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;
import com.example.reading_app.web.controller.dto.UserEditForm;
import com.example.reading_app.web.controller.dto.UserRegisterForm;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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

    // ユーザー情報更新処理
    @Transactional
    public void updateProfile(Long userId, UserEditForm form) {
        User u = findById(userId);
        u.setDifficulty(form.getDifficulty());
        u.setWordCount(form.getWordCount());
        u.setQuestionCount(form.getQuestionCount());
        u.setTopic(form.getTopic());
        userRepository.save(u);
    }
}
