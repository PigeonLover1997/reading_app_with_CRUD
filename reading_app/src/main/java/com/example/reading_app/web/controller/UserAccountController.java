package com.example.reading_app.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.reading_app.web.controller.dto.DeleteAccountForm;
import com.example.reading_app.web.controller.dto.UserAccountForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * このコントローラは、ログイン中のユーザーが
 * ・ユーザー名／パスワードの変更
 * ・アカウント削除（論理削除）
 * を行うための画面表示・フォーム処理を担当する。
 */
@Controller
@RequestMapping("/user/account")
public class UserAccountController {
    // ユーザー自身によるユーザー情報（ユーザー名・パスワード）の編集用クラス

    @Autowired
    private UserService userService;

    // 編集ページ表示
    @GetMapping
    public String showAccountForm(@AuthenticationPrincipal CustomUserDetails loginUser, Model model) {
        // 現在のユーザー情報を取得して、フォームに初期値としてセット
        User user = userService.findById(loginUser.getId());
        UserAccountForm form = new UserAccountForm();
        form.setUsername(user.getUsername());// ユーザー名をフォームにセット
        model.addAttribute("userAccountForm", form);
        return "user_account_edit";
    }

    /**
     * ユーザー名／パスワード変更の処理（POST）
     */
    @PostMapping
    public String updateAccount(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @ModelAttribute("userAccountForm") @Validated UserAccountForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        // 入力エラーがあればフォームを再表示
        if (result.hasErrors()) {
            return "user_account_edit";
        }
        // サービスを通じて更新処理
        boolean updated = userService.updateAccount(loginUser.getId(), form);
        if (!updated) {
            model.addAttribute("passwordError", "現在のパスワードが違います");
            return "user_account_edit";
        }

        redirectAttributes.addFlashAttribute("updateSuccess", "ユーザー名・パスワードを変更しました");
        return "redirect:/user/home";
    }

    // アカウント削除ページ表示
    @GetMapping("/delete")
    public String showDeleteForm(Model model) {
        model.addAttribute("deleteForm", new DeleteAccountForm());
        return "user_account_delete";
    }

    // アカウント削除処理
    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal CustomUserDetails loginUser,
            @ModelAttribute DeleteAccountForm deleteForm,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        // パスワードが一致するか確認しながら削除処理を実行
        boolean ok = userService.deleteAccount(loginUser.getId(), deleteForm.getPassword());
        if (!ok) {
            result.rejectValue("password", "error.password", "パスワードが正しくありません");
            return "user_account_delete";
        }

        // 成功したら、削除したユーザー名をメッセージに渡す
        String deletedUsername = loginUser.getUsername();
        redirectAttributes.addFlashAttribute("deletedAccountMessage", "ユーザー名：" + deletedUsername + " のアカウントを削除しました");

        // ログイン状態をクリアした上でログイン画面へリダイレクト
        // 明示的にログアウトさせる
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
