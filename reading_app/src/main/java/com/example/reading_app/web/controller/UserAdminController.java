package com.example.reading_app.web.controller;
import com.example.reading_app.domain.model.User;
import com.example.reading_app.web.controller.dto.AdminUserEditForm;
import com.example.reading_app.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    @Autowired
    private UserService userService;

    // 難易度リスト等の共通データ
    private final List<String> levels = List.of("Below A1", "A1", "A2", "B1", "B2", "C1", "C2", "Over C2");
    private final List<Integer> defaultWordCounts = List.of(100, 200, 300, 400, 500);
    private final List<Integer> defaultQuestionCounts = List.of(1, 2, 3, 4, 5);

    // ユーザー一覧を取得し、一覧ページを表示
    @GetMapping
    public String listUsers(Model model, @ModelAttribute("updateSuccess") String updateSuccess) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        // 更新メッセージがFlashAttributeで来た場合表示
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

        model.addAttribute("adminUserEditForm", form);
        model.addAttribute("userId", id);
        model.addAttribute("levels", levels);
        model.addAttribute("defaultWordCounts", defaultWordCounts);
        model.addAttribute("defaultQuestionCounts", defaultQuestionCounts);

        return "admin_user_edit";
    }

    // 編集保存
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("adminUserEditForm") @Validated AdminUserEditForm form,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            // levels等再設定
            model.addAttribute("levels", levels);
            model.addAttribute("defaultWordCounts", defaultWordCounts);
            model.addAttribute("defaultQuestionCounts", defaultQuestionCounts);
            model.addAttribute("userId", id);
            return "admin_user_edit";
        }
        userService.updateUserByAdmin(id, form);
        redirectAttributes.addFlashAttribute("updateSuccess", "ユーザー情報を更新しました");
        return "redirect:/admin/users";
    }

    // ユーザー削除
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("updateSuccess", "ユーザーを削除しました");
        return "redirect:/admin/users";
    }
}
