package com.example.reading_app.web.controller;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.service.UserService;
import com.example.reading_app.web.controller.dto.DeleteAccountForm;
import com.example.reading_app.web.controller.dto.UserAccountForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.reading_app.config.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/account")
public class UserAccountController {
// ユーザー自身によるユーザー情報（ユーザー名・パスワード）の編集用クラス

    @Autowired
    private UserService userService;

    // 編集ページ表示
    @GetMapping
    public String showAccountForm(@AuthenticationPrincipal CustomUserDetails loginUser, Model model) {
        User user = userService.findById(loginUser.getId());
        UserAccountForm form = new UserAccountForm();
        form.setUsername(user.getUsername());
        model.addAttribute("userAccountForm", form);
        return "user_account_edit";
    }

    // 編集保存
    @PostMapping
    public String updateAccount(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @ModelAttribute("userAccountForm") @Validated UserAccountForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            return "user_account_edit";
        }

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

        boolean ok = userService.deleteAccount(loginUser.getId(), deleteForm.getPassword());
        if (!ok) {
            result.rejectValue("password", "error.password", "パスワードが正しくありません");
            return "user_account_delete";
        }

    // ユーザー名をメッセージにセット
    String deletedUsername = loginUser.getUsername();
    redirectAttributes.addFlashAttribute("deletedAccountMessage", "ユーザー名：" + deletedUsername + " のアカウントを削除しました");

    // ログイン状態をクリアした上でログイン画面へリダイレクト
    // 明示的にログアウトさせる
    SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
