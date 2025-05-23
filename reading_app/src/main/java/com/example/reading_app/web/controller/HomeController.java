package com.example.reading_app.web.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.reading_app.domain.model.User;
import com.example.reading_app.domain.model.dto.FeedbackResultDto;
import com.example.reading_app.domain.model.dto.ReadingTaskDto;
import com.example.reading_app.domain.repository.UserRepository;
import com.example.reading_app.domain.service.FeedbackGenerationService;
import com.example.reading_app.domain.service.TaskGenerationService;
import com.example.reading_app.domain.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@SessionAttributes("task") // セッションにReadingTaskDtoを保持 問題採点時にも使用できるようにセッションに保存
public class HomeController {

    @Autowired
    private TaskGenerationService taskService;

    @Autowired
    private FeedbackGenerationService feedbackService;

    @Autowired
    private ObjectMapper objectMapper; // Jacksonのマッパーを注入

    @Autowired
    private UserService userService;

    /** ゲスト用トップページ（フォーム入力画面） */
    @GetMapping("/")
    public String showGuestHome(Model model) {
        // 難易度リスト
        model.addAttribute("levels", List.of("Below A1", "A1", "A2", "B1", "B2", "C1", "C2", "Over C2"));
        // 語数のテンプレートリスト
        model.addAttribute("defaultWordCounts", List.of(100, 200, 300, 400, 500));
        // 問題数のテンプレートリスト
        model.addAttribute("defaultQuestionCounts", List.of(1, 2, 3, 4, 5));
        return "index";
    }

    /** ユーザー用トップページ（ログイン後のフォーム入力画面）（内容は基本的にはゲストと同じ）*/
    @GetMapping("/user/home")
    // java.security.Principalは現在認証されているユーザーを表し、Spring Securityでは、自動的に現在のログインユーザーをこのPrincipal型で受け取れる
    public String showUserHome(Model model, Principal principal) {
        // 難易度リスト
        model.addAttribute("levels", List.of("Below A1", "A1", "A2", "B1", "B2", "C1", "C2", "Over C2"));
        // 語数のテンプレートリスト
        model.addAttribute("defaultWordCounts", List.of(100, 200, 300, 400, 500));
        // 問題数のテンプレートリスト
        model.addAttribute("defaultQuestionCounts", List.of(1, 2, 3, 4, 5));

        // principal.getName()でユーザー名を取得し、登録
        model.addAttribute("username", principal.getName());

    // ユーザーごとの語数などの初期値を取得（Userオブジェクトなどから取得）
    Optional<User> user = userService.findByUsername(principal.getName());

    if (user.isPresent()) {
        model.addAttribute("initDifficulty", user.get().getDifficulty());
        model.addAttribute("initWordCount", user.get().getWordCount());
        model.addAttribute("initQuestionCount", user.get().getQuestionCount());
        model.addAttribute("initTopic", user.get().getTopic());
    }

        return "user_home";
    }

    /**
     * フォーム送信後に呼ばれる
     * 各種パラメータを受け取り Task を生成 → task_play.html へ遷移
     */
    @PostMapping("/generate")
    public String generateTask(
            @RequestParam("difficulty") String difficulty,
            @RequestParam("wordCountRadio") int wordCountRadio,
            @RequestParam(value = "customWordCount", required = false) Integer customWordCount,
            @RequestParam("questionCountRadio") int questionCountRadio,
            @RequestParam(value = "customQuestionCount", required = false) Integer customQuestionCount,
            @RequestParam(value = "topic", required = false) String topic,
            Model model) {
        // ラジオが 0 のときだけ自由入力、それ以外はラジオ値
        int wordCount = (wordCountRadio == 0 && customWordCount != null && customWordCount > 0)
                ? customWordCount
                : wordCountRadio;

        int questionCount = (questionCountRadio == 0 && customQuestionCount != null && customQuestionCount > 0)
                ? customQuestionCount
                : questionCountRadio;

        if (topic == null)
            topic = "";

        ReadingTaskDto task = taskService.generateTask(difficulty, wordCount, questionCount, topic);
        model.addAttribute("task", task); // セッションに保持される
        return "task_play";
    }

    /**
     * task_play.html のフォームから POST される全回答を受け取り、
     * ChatGPT に採点依頼 → 解析 → feedback.html に渡す
     */
    @PostMapping("/submitAnswers")
    public String submitAnswers(
            @ModelAttribute("task") ReadingTaskDto task, // セッション or hidden で保持していた Task
            @RequestParam("compositionAnswer") String compositionAnswer,
            @RequestParam Map<String, String> params, // mcq_0, mcq_1, … をまとめて受け取る
            @RequestParam("passageWordCount") int passageWordCount, // 課題文の語数
            Model model) throws Exception {
        // 1) 選択式解答を A/B/C/D の文字列リストにまとめる
        List<String> userAnswers = new ArrayList<>();
        for (int i = 0; i < task.getMcqs().size(); i++) {
            String key = "mcq_" + i; // フォーム側の name 属性に対応
            String ans = params.get(key);
            if (ans == null) {
                // 未選択時は空文字
                ans = "";
            }
            userAnswers.add(ans); // 例: "A", "C", "B"…
        }
        // 2) サービス呼び出し（JSON→DTO まで完結する想定）
        FeedbackResultDto feedback = feedbackService.gradeAndFeedback(task, userAnswers, compositionAnswer);

        // 3) View が参照する名前で model に登録
        model.addAttribute("passageWordCount", passageWordCount); // 課題文の語数
        model.addAttribute("task", task);
        model.addAttribute("userAnswers", userAnswers);
        model.addAttribute("compositionAnswer", compositionAnswer);
        model.addAttribute("feedback", feedback);
        return "feedback";
    }
}
