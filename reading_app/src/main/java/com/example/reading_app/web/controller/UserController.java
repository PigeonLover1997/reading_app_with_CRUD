package com.example.reading_app.web.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.reading_app.config.CustomUserDetails;
import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.service.UserService;
import com.example.reading_app.web.controller.dto.UserEditForm;

/**
 * このコントローラは、「ユーザー本人のプロフィール編集」に関する画面遷移・処理を担当。
 * Webブラウザからのリクエスト（GET/POST）を受け取り、サービス層を通してロジックを実行し、
 * HTMLテンプレートを返す。
 */
@Controller
@RequestMapping("/user") // このコントローラのすべてのURLに /user をプレフィックスとして付ける
public class UserController {

    // UserService 用のフィールドを追加
    private final UserService userService;

    // ここで Spring が UserService を渡してくれる（コンストラクタインジェクション）（@Autowired は省略可能）
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @ModelAttribute メソッドは、コントローラ内で定義したオブジェクトや値を、
    // Thymeleafなどのテンプレートで使えるようにするためのアノテーション

    // 0730ここまでコメント付加済

    // （登録時と共通の選択肢リストを使うなら、@ModelAttribute メソッドで共通化も可）
    @ModelAttribute("levels")
    public List<String> levels() {
        return List.of("Below A1", "A1", "A2", "B1", "B2", "C1", "C2", "Over C2");
    }

    @ModelAttribute("defaultWordCounts")
    public List<Integer> defaultWordCounts() {
        return List.of(100, 200, 300, 400, 500);
    }

    @ModelAttribute("defaultQuestionCounts")
    public List<Integer> defaultQuestionCounts() {
        return List.of(1, 2, 3, 4, 5);
    }

    /** プロフィール編集フォームを表示 */
    @GetMapping("/edit") // クラスについている共通部分@RequestMapping("/user")の続き
    public String showEditForm(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            Model model) {
        // 確認用
        // この時点で loginUser が null かどうかをチェック
        System.out.println("loginUser via @AuthenticationPrincipal: " + loginUser);

        // SecurityContextHolder 経由で Authentication を取得してみる
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            System.out.println(">>> SecurityContextHolder.getContext().getAuthentication() is null");
        } else {
            System.out.println(">>> Authentication class: " + authentication.getClass().getName());
            Object principal = authentication.getPrincipal();
            System.out.println(">>> principal class: " + principal.getClass().getName());
            System.out.println(">>> principal.toString(): " + principal);
        }

        // ここで loginUser が null の場合はリダイレクトなどの処理を入れると安心です
        if (loginUser == null) {
            return "redirect:/login";
        }

        // （以下、元の処理）

        // DB から現在のユーザー情報を取得
        User user = userService.findById(loginUser.getId());
        // UserEditForm に現在のユーザー情報を設定
        UserEditForm form = new UserEditForm();
        form.setUsername(user.getUsername());
        form.setDifficulty(user.getDifficulty());
        form.setWordCount(user.getWordCount());
        form.setQuestionCount(user.getQuestionCount());
        form.setTopic(user.getTopic());
        // パスワードはここでは設定しない

        model.addAttribute("userEditForm", form);
        model.addAttribute("isEdit", true);
        return "user_edit"; // 登録ページを流用
    }

    /** プロフィール更新処理 */
    @PostMapping("/edit")
    public String updateProfile(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @ModelAttribute("userEditForm") @Validated UserEditForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

if (result.hasErrors()) {
    result.getFieldErrors().forEach(error -> {
        System.out.println("フィールド: " + error.getField() +
            ", エラー内容: " + error.getDefaultMessage());
    });
    result.getGlobalErrors().forEach(error -> {
        System.out.println("グローバルエラー: " + error.getDefaultMessage());
    });
    return "user_edit";
}

        userService.updateProfile(loginUser.getId(), form);

        redirectAttributes.addFlashAttribute("updateSuccess", "プロフィールを更新しました");
        return "redirect:/user/home"; // 編集後はマイページなどへリダイレクト
    }
}
