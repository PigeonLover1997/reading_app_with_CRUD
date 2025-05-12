package com.example.reading_app.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.reading_app.domain.model.ReadingTaskDto;
import com.example.reading_app.domain.service.TaskGenerationService;

@Controller
public class HomeController {

    @Autowired
    private TaskGenerationService taskService;

    /** トップページ（フォーム入力画面） */
    @GetMapping("/")
    public String showForm(Model model) {
        // 難易度リスト
        model.addAttribute("levels", List.of("A1", "A2", "B1", "B2", "C1", "C2"));
        // 語数のテンプレートリスト
        model.addAttribute("defaultWordCounts", List.of(200, 300, 400, 500));
        // 問題数のテンプレートリスト
        model.addAttribute("defaultQuestionCounts", List.of(1, 2, 3, 4));
        return "index";
    }

    /**
     * フォーム送信後に呼ばれる
     * 各種パラメータを受け取り Task を生成 → taskPlay.html へ遷移
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
        int wordCount = (customWordCount != null && customWordCount > 0)
                ? customWordCount
                : wordCountRadio;
        int questionCount = (customQuestionCount != null && customQuestionCount > 0)
                ? customQuestionCount
                : questionCountRadio;
        if (topic == null)
            topic = "";

        ReadingTaskDto task = taskService.generateTask(difficulty, wordCount, questionCount, topic);
        model.addAttribute("task", task);
        return "taskPlay";
    }
}
