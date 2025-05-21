package com.example.reading_app.domain.service;

import com.example.reading_app.domain.model.ReadingTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import com.example.reading_app.util.TopicPool; // ランダムトピック取得用

/**
 * ChatGPT API を呼び出し、
 * ・指定 CEFR レベル・語数・トピック の長文読解パッセージ
 * ・指定数の選択式問題（4 選択肢＋正解インデックス）
 * ・英作文問題用の指示文
 * をまとめて生成し、Java オブジェクトとして返すサービス
 */
@Service
public class TaskGenerationService {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ユーザー指定のパラメータでタスクを生成
     *
     * @param difficulty    CEFR レベル ("Below A1"～"Over C2")
     * @param wordCount     パッセージの語数（おおよそ）
     * @param questionCount 選択式問題の数
     * @param topic         トピック（空文字の場合、TopicPoolクラスからランダムで取得）
     * @return ReadingTaskDto オブジェクト（問題文・タイトル・難易度・選択式問題リスト・英作文課題用プロンプトを含む）
     */
    public ReadingTaskDto generateTask(String difficulty, int wordCount, int questionCount, String topic) {

        // ① ChatGPT に与えるシステム指示
        String systemPrompt = "You are an expert English writer whose articles are famous for their interesting content. "
                + "Your task is to generate an interesting reading passage and multiple-choice questions for English learners. "
                + "The passage should be in multiple paragraphs, using clear paragraph breaks. "
                + "Insert a blank line (double line break) between paragraphs for readability, and begin each paragraph with two spaces for indentation. "
                + "Generate an English reading passage at CEFR level " + difficulty
                + " on the topic '" + (topic.isBlank() ? TopicPool.getRandomTopic() : topic) + "'. "
                + "When you are instructed to do \"Below A1\" level, use extremely easy English words and basic grammar, and short simple sentences for beginner level English learners who are not ready for CEFL A1 level. "
                + "When you are instructed to do \"Over C2\" level, use syntax so absurdly complex and vocabulary so arcane that even well-educated, native English-speaking adults would struggle to understand it. "
                // 語数指定を確認  語数が多くなると指定より出力が少なくなる傾向があるので、指定語数に応じて補正をかける
                + "Check the following designated word count: " + wordCount + ". "
                // 語数指定が ～150の場合は、指定語数の 0.9倍～1.1 倍になるように指示
                + "If the designated word count is less than 150, the passage **must have between "
                + (int) (wordCount * 0.9) + " and " + (int) (wordCount * 1.1) + " English words**. "
                // 語数指定が 151～300の場合は、指定語数の 1.1倍～1.3 倍になるように指示
                + "Else if the designated word count is between 151 and 300, the passage **must have between "
                + (int) (wordCount * 1.1) + " and " + (int) (wordCount * 1.3) + " English words**. "
                // 語数指定が 301～450の場合は、指定語数の 1.3倍～1.5 倍になるように指示
                + "Else if the designated word count is between 301 and 450, the passage **must have between "
                + (int) (wordCount * 1.3) + " and " + (int) (wordCount * 1.5) + " English words**. "
                // 語数指定が 451～600の場合は、指定語数の 1.7倍～2.0 倍になるように指示
                + "Else if the designated word count is between 451 and 600, the passage **must have between "
                + (int) (wordCount * 1.7) + " and " + (int) (wordCount * 2.0) + " English words**. "
                // 語数指定が 601～の場合は、指定語数の 2.0倍～2.5 倍になるように指示
                + "Else if the designated word count is 601 or more, the passage **must have between "
                + (int) (wordCount * 2.0) + " and " + (int) (wordCount * 2.5) + " English words**. "
                + "Use the following method to count words: count the number of sequences that match the regular expression \\b\\w+\\b in the passage. "
                + "Plus, generate a title for the passage."
                + "Also, when you pick from a broad category (e.g. culture), select a different specific subtopic each time for variety. "
                + "Then create " + questionCount + " multiple-choice questions (4 options each) "
                + "and provide a prompt for the test taker to practice English writing composition, based on the contents of the passage and appropriate difficulty for an English learner whose goal is at CEFR level " + difficulty
                + "When you are instructed to make two or more questions, make the first one vocabulary inference quiz(e.g. The word/phrase \"difficult word or phrase from the passage\" is closest in meaning to:) . "
                + "When you are instructed to make only one question, prefer other types of question. "
                + "Even if the topic is given in Japanese, always generate the passage, multiple-choice questions and the composition prompt in English. "
                + "Incorrect options must be highly misleading unless the test taker fully understands the passage; for example, they may be generally true but not supported by the passage. "
                + "Adding " + difficulty + " as cefrLevel, respond in JSON with keys: "
                + "\"title\" (string), "
                + "\"passage\" (string), "
                + "\"cefrLevel\" (string), "
                + "\"mcqs\" (array of {question:string, options:[string], answerLabel:string}), "
                + "\"compositionPrompt\" (string).";

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
