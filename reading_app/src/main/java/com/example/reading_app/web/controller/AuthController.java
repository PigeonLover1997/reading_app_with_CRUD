package com.example.reading_app.web.controller;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.reading_app.web.controller.dto.UserForm;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ユーザー登録フォーム表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register"; // templates/register.html
    }

    // ユーザー登録処理
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("userForm") UserForm userForm,
                                  RedirectAttributes redirectAttributes) {
        // ユーザー名重複チェック
        if (userRepository.findByUsername(userForm.getUsername()).isPresent()) {
            // 重複する場合エラーメッセージを登録して登録フォームページにリダイレクト
            // リダイレクト先で${error}で参照可能
            redirectAttributes.addFlashAttribute("error", "このユーザー名はすでに使われています");
            return "redirect:/register";
        }
        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userForm.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "ユーザー登録が完了しました！");
        return "redirect:/login";
    }

    // ログインフォーム表示
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }
}
