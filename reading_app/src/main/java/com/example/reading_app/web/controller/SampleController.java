// 使わないクラス？？
package com.example.reading_app.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.reading_app.domain.service.SampleChatGPTService;

@Controller
public class SampleController {

    @Autowired
    private SampleChatGPTService sampleChatGPTService;

    // 入力フォーム画面を表示（HTTP GETリクエスト）
    @GetMapping("/sample")
    public String showForm() {
        return "index";  // src/main/resources/templates/index.html を表示
    }

    // フォーム送信（HTTP POSTリクエスト）を処理し、質問生成結果をモデルに詰めて返す
    @PostMapping("/sample")
    public String generateQuestion(@RequestParam("userInput") String userInput, Model model) {
        // サービスを呼び出して質問文を取得
        //String question = sampleChatGPTService.generateQuestion(userInput); // 本来はこちらを使用
        String question = sampleChatGPTService.generateQuestion(); // テスト用にサンプルデータの決め打ちを使用
        // モデルに結果を追加し、ビューに渡す
        model.addAttribute("question", question);
        return "index";  // 処理後も同じ入力フォームページ(index.html)を表示
    }
}