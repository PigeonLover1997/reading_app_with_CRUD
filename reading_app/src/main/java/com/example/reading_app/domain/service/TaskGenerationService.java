package com.example.reading_app.domain.service;

import com.example.reading_app.domain.model.MCQDto;
import com.example.reading_app.domain.model.ReadingTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * ChatGPT API を呼び出し、
 * ・指定 CEFR レベル・語数・トピック の長文読解パッセージ
 * ・指定数の選択式問題（4 選択肢＋正解インデックス）
 * ・要約問題用のプロンプト
 * をまとめて生成し、Java オブジェクトとして返すサービス
 */
@Service
public class TaskGenerationService {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ユーザー指定のパラメータでタスクを生成
     *
     * @param difficulty CEFR レベル ("A1"～"C2")
     * @param wordCount  パッセージの語数（おおよそ）
     * @param questionCount 選択式問題の数
     * @param topic      トピック（空文字でランダム）
     * @return Task オブジェクト（パッセージ・MCQリスト・要約プロンプトを含む）
     */
    public ReadingTaskDto generateTask(String difficulty, int wordCount, int questionCount, String topic) {
        // ① ChatGPT に与えるシステム指示
        String systemPrompt = "You are an English teacher. "
            + "Generate an English reading passage of about " + wordCount + " words at CEFR level " + difficulty
            + " on the topic '" + (topic.isBlank() ? "a random topic" : topic) + "'. "
            + "Then create " + questionCount + " multiple-choice questions (4 options each) "
            + "and provide a summary question prompt. "
            + "Even if the topic is given in Japanese, always respond in English. "
            + "Respond in JSON with keys: "
            + "\"passage\" (string), "
            + "\"mcqs\" (array of {question:string, options:[string], answerIndex:int}), "
            + "\"summaryPrompt\" (string).";

        // ② リクエストボディを構築
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        body.put("messages", messages);

        // ③ HTTP ヘッダー設定
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ④ API 呼び出し
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject(apiUrl, request, Map.class);

        // ⑤ レスポンスから JSON 部分を抽出し、Task オブジェクトにパース
        List<?> choices = (List<?>) response.get("choices");
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) ((Map<String, ?>) choices.get(0)).get("message");
        String content = (String) message.get("content");

        try {
            // content は JSON 文字列のはずなので、そのまま ReadingTaskDto クラスにマッピング
            return objectMapper.readValue(content, ReadingTaskDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ChatGPT response", e);
        }
    }
}
