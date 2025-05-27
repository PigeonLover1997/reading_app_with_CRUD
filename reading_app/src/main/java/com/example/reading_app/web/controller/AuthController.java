package com.example.reading_app.web.controller;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.reading_app.web.controller.dto.UserRegisterForm;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ユーザー登録フォーム表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // ユーザー登録フォーム用のDTOを作成し、初期値（画面で選択済の値）を設定
        UserRegisterForm userForm = new UserRegisterForm();
        userForm.setDifficulty("A1");
        userForm.setWordCount(200);
        userForm.setQuestionCount(2);
        // トピックはユーザー登録段階では初期値なしだが、将来的にユーザー情報編集画面でも同じ構造で設定できるようにする
        // いずれの項目も、ユーザー情報編集画面では現在の設定値（user.getDifficulty()など）を初期値にする想定
        userForm.setTopic(null);
        model.addAttribute("userRegisterForm", userForm);
        // 希望条件選択用のリスト項目
        model.addAttribute("levels", List.of("Below A1","A1","A2","B1","B2","C1","C2","Over C2"));
        model.addAttribute("defaultWordCounts", List.of(100,200,300,400,500));
        model.addAttribute("defaultQuestionCounts", List.of(1,2,3,4,5));
        return "register"; // templates/register.html
    }

    // ユーザー登録処理
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("userForm") UserRegisterForm userForm,
                                  RedirectAttributes redirectAttributes) {
        // ユーザー名重複チェック
        if (userRepository.findByUsername(userForm.getUsername()).isPresent()) {
            // 重複する場合エラーメッセージを登録して登録フォームページにリダイレクト
            // リダイレクト先で${error}で参照可能
            redirectAttributes.addFlashAttribute("error", "このユーザー名はすでに使われています");
            return "redirect:/register";
        }
        User user = new User();
        // ユーザー情報をDTOから取得して設定
        user.setUsername(userForm.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userForm.getPassword()));
        user.setDifficulty(userForm.getDifficulty());
        user.setWordCount(userForm.getWordCount());
        user.setQuestionCount(userForm.getQuestionCount());
        user.setTopic(userForm.getTopic());
        // ユーザーの役割を設定（デフォルトは一般ユーザー）
        user.setRole("USER");
        userRepository.save(user);

        // 登録完了メッセージ＋ユーザー名をフラッシュ属性で渡す
        // addFlashAttributeを使っているので、/loginにリダイレクトした直後の1回だけ表示される
    redirectAttributes.addFlashAttribute("registerSuccessMessage",
        // String.format("...%s...", 変数)の書式を使うことで、%sのところに変数の値（ここではユーザー名）が自動で入る
        String.format("ユーザー登録が完了しました。（ユーザー名：%s） 引き続きログインする場合は認証情報を入力してください", userForm.getUsername())
    );
    return "redirect:/login";
    }

    // ログインフォーム表示
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }
}
