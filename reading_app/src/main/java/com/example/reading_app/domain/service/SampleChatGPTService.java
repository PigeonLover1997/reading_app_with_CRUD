package com.example.reading_app.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.*;

// Springのサービスクラスとして定義
// @Serviceアノテーションを付与することで、Springがこのクラスを管理する
// 必要に応じて他のクラスから@Autowired（依存性注入）できるようになる
@Service
public class SampleChatGPTService {

    // application.propertiesから値を読み込む
    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    // RestTemplate: シンプルなHTTPクライアント
    private final RestTemplate restTemplate = new RestTemplate();

    // システムメッセージ（ChatGPTへの指示）
    private static final String systemPrompt = "You are an English language test creator. Please create a reading passage and comprehension questions based on the following conditions:\r\n"
            + //
            "\r\n" + //
            "- CEFR level: {CEFR_LEVEL}" + // Userが指定するCEFRレベル（例: A1, A2, B1, B2, C1, C2）
            "- Approximate word count: {WORD_COUNT}\r\n" + // Userが指定する語数（例: 100, 200, 300）
            "- Number of multiple-choice questions: {QUESTION_COUNT}\r\n" + // Userが指定する問題数（例: 2, 3, 4）
            "- Topic: {TOPIC}\r\n" + // Userが指定するトピック（例: travel, food, culture）
            "- Each question should have 4 answer options (A, B, C, D)\r\n" + //
            "- The correct answer should appear in a random position for each question\r\n" + //
            "- After each question, clearly indicate the correct answer with: Correct answer: (e.g., \"Correct answer: C\")\r\n"
            + //
            "\r\n" + //
            "Return the result in plain text only, using this format:\r\n" + //
            "\r\n" + //
            "---\r\n" + //
            "\r\n" + //
            "[Reading passage]\r\n" + //
            "\r\n" + //
            "---\r\n" + //
            "\r\n" + //
            "Q1. [Question 1 text]  \r\n" + //
            "A. [Option A]  \r\n" + //
            "B. [Option B]  \r\n" + //
            "C. [Option C]  \r\n" + //
            "D. [Option D]  \r\n" + //
            "Correct answer: [A/B/C/D]\r\n" + //
            "\r\n" + //
            "Q2. ...\r\n" + //
            "\r\n" + //
            "Q3. ...";

            // ユーザーの入力内容サンプル（ChatGPTへの指示）
    private static final String userInputSample = 
            "- CEFR level: B1" + // Userが指定するCEFRレベル（例: A1, A2, B1, B2, C1, C2）
            "- Approximate word count: 200\r\n" + // Userが指定する語数（例: 100, 200, 300）
            "- Number of multiple-choice questions: 2\r\n" + // Userが指定する問題数（例: 2, 3, 4）
            "- Topic: Technology\r\n"; // Userが指定するトピック（例: travel, food, culture）

    /**
     * ユーザーからの入力テキストを元に、ChatGPT APIを呼び出して質問文を生成する
     */
    //public String generateQuestion(String userInput) { // 実際はこちらを使用予定
    public String generateQuestion() { // テスト用に引数なしで、userInputSampleを固定値として実行
        // 1. HTTPリクエストヘッダーの作成
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);  // 認証ヘッダーにBearerトークンとしてAPIキーを設定

        // 2. リクエストボディの作成（JSON相当のMapを構築）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        // messages配列を作成: systemとuserの2つのメッセージを含む
        // messages配列は、AIにメッセージの履歴を渡すために使用される
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userInputSample));
        requestBody.put("messages", messages);
        // ※必要に応じてmax_tokensやtemperatureを設定できます（今回はデフォルト）

        // 3. HTTPリクエストの発行
        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);
        try {
            // APIにPOSTリクエストを送り、レスポンスボディをMapとして受け取る
            Map response = restTemplate.postForObject(apiUrl, httpRequest, Map.class);
            // 4. レスポンス解析: 質問文を抽出
            // choicesはアプリ内で定義した名前ではなく、OpenAI APIの仕様上、choices配列の要素として返答メッセージが返ってくる
            if (response == null || !response.containsKey("choices")) {
                // レスポンスがnullまたはchoicesキーが存在しない場合はエラー
                throw new RuntimeException("No response from OpenAI API");
            }
            // choices配列の先頭要素からmessage.contentを取得
            List<?> choices = (List<?>) response.get("choices");
            if (choices.isEmpty()) {
                // choices配列が空の場合はエラー
                throw new RuntimeException("No choices in OpenAI response");
            }
            // choices配列の先頭要素からmessage.contentを取得
            Map<?,?> firstChoice = (Map<?,?>) choices.get(0);
            // messageキーからメッセージを取得
            Map<?,?> message = (Map<?,?>) firstChoice.get("message");
            String question = (String) message.get("content");
            return question;
        } catch (RestClientException e) {
            e.printStackTrace();
            // エラー発生時は簡易的にメッセージを返す（本来はユーザー向けメッセージなど検討）
            return "※エラー: 問題を生成できませんでした。";
        }
    }
}