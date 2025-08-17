package com.example.reading_app.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.service.UserService;
import com.example.reading_app.web.controller.dto.AdminUserEditForm;

/**
 * 管理者用のコントローラ。
 * - ユーザー一覧の表示
 * - 個別ユーザーの編集・保存・削除
 * を担当する。
 */
@Controller
@RequestMapping("/admin/users") // すべてのURLが /admin/users から始まる
public class UserAdminController {

    @Autowired
    private UserService userService;

    // ユーザー編集画面で使うプルダウン選択肢（難易度や語数など）を定義
    private final List<String> levels = List.of("Below A1", "A1", "A2", "B1", "B2", "C1", "C2", "Over C2");
    private final List<Integer> defaultWordCounts = List.of(100, 200, 300, 400, 500);
    private final List<Integer> defaultQuestionCounts = List.of(1, 2, 3, 4, 5);

    /**
     * ユーザー一覧ページを表示。
     * - 登録されているすべてのユーザーを表示
     * - 更新や削除後に表示されるメッセージも処理
     */
    @GetMapping
    public String listUsers(Model model, @ModelAttribute("updateSuccess") String updateSuccess) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        // 更新メッセージがFlashAttributeで来た場合表示
        // FlashAttributeは、リダイレクト後の1回限りで使える一時的なデータのこと
        if (updateSuccess != null && !updateSuccess.isEmpty()) {
            model.addAttribute("updateSuccess", updateSuccess);
        }
        return "admin_user_list";
    }

    // 該当IDのユーザーの編集フォーム表示
    @GetMapping("/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        AdminUserEditForm form = new AdminUserEditForm();
        // user→formへ値セット
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        form.setIsActive(user.getIsActive());
        form.setDifficulty(user.getDifficulty());
        form.setWordCount(user.getWordCount());
        form.setQuestionCount(user.getQuestionCount());
        form.setTopic(user.getTopic());
        // パスワードは空欄（編集時に入力があれば更新、空欄ならそのまま）

        // テンプレートに渡す情報をセット
        model.addAttribute("adminUserEditForm", form);
        model.addAttribute("userId", id);
        model.addAttribute("levels", levels);
        model.addAttribute("defaultWordCounts", defaultWordCounts);
        model.addAttribute("defaultQuestionCounts", defaultQuestionCounts);

        return "admin_user_edit";
    }

    // ユーザー情報の編集内容を保存する
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
            @ModelAttribute("adminUserEditForm") @Validated AdminUserEditForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            // エラー時も選択肢リストが必要なので再設定
            model.addAttribute("levels", levels);
            model.addAttribute("defaultWordCounts", defaultWordCounts);
            model.addAttribute("defaultQuestionCounts", defaultQuestionCounts);
            model.addAttribute("userId", id);
            return "admin_user_edit";// 入力画面に戻る
        }
        userService.updateUserByAdmin(id, form);
        redirectAttributes.addFlashAttribute("updateSuccess", "ユーザー情報を更新しました");
        return "redirect:/admin/users";
    }

    // 管理者によるユーザー削除（物理削除）
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("updateSuccess", "ユーザーを削除しました");
        return "redirect:/admin/users";
    }
}
