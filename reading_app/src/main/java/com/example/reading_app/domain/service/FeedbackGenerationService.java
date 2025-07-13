package com.example.reading_app.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.reading_app.domain.model.dto.FeedbackResultDto;
import com.example.reading_app.domain.model.dto.MCQDto;
import com.example.reading_app.domain.model.dto.ReadingTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FeedbackGenerationService {
    /**
     * OpenAI API を利用して、ユーザーの解答を採点・フィードバックするサービス
     * 
     * @param task    元のReadingTaskDtoオブジェクト（passage, mcqs, compositionPrompt）
     * @param answers 選択式回答のリスト (選択肢インデックス)
     * @param composition 英作文の自由入力
     * @return JSON文字列のまま返却し、Controllerにてパース
     */
@Autowired
 private ObjectMapper objectMapper;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate rest = new RestTemplate();

    /**
     * ユーザーの解答を採点・フィードバック
     *
     * @param task    元のTaskオブジェクト（title, difficulty, passage, mcqs, compositionPrompt）
     * @param answers 選択式回答のリスト (選択肢インデックス)
     * @param composition 英作文の自由入力
     * @return JSON文字列のまま返却し、Controllerにてパース
     */
    public FeedbackResultDto gradeAndFeedback(ReadingTaskDto task, List<String> answers, String composition) {
        // 英作文採点時に使用する難易度情報
        String cefrLevel = task.getCefrLevel();

        // システム指示
        String system = "You are an expert English teacher who teaches English to Japanese students. "
                +"First, grade each multiple-choice answer: for each question, provide whether it's correct or not, and provide a justification section with the following format: 本文中の該当箇所: a direct quote from the passage that supports the correct answer in double quotes as the evidence (Japanese translation of that part in parentheses)\n"
                +"解説: (Clear explanation of why the correct answer is correct, and why each incorrect option is incorrect, if applicable.). "
                +"Then, evaluate the student's English composition on two criteria (grammar and appropriate word usage, content) on a 0-10 scale. 0: Not answered, 1-2: Poor, 3-4: Below Average, 5-6: Average, 7-8: Good, 9-10: Excellent. "
                +"Describe why you gave that score, and overall feedback as the score commentary. "
                +"The task's difficulty level in CEFR is " + cefrLevel + ". "
                +"When evaluating the English composition, always take into account the CEFR level of the task. If the CEFR level is low (such as A1 or A2), award higher scores for the same level of content and English, but if the CEFR level is high (such as C1 or C2), apply stricter grading criteria. Adjust your grammar and word usage score and content scores according to the expected performance at each CEFR level.\n" 
                +"Also, DO NOT refer to the multiple-choice answers. The English composition grading must be based on how well the student's writing answers the English composition task.\n"
                +"Also point out errors in grammar and word usage, suggest corrections and why the original expression was incorrect, and finally show a couple of useful expressions related to the passage that are useful for the student to use in writing or speaking, with Japanese explanation for each phrase. "
                +"Respond entirely in Japanese because estimated test takers are Japanese people who learn English.\n"
                +"Respond in JSON with keys as follows:\n"
                +"  mcqResults: [{questionIndex:int, correct:boolean, evidence:string, explanation:string}],\n"
                +"  compositionScores: {grammarAndUsageScore:int, contentScore:int, scoreCommentary:string},\n"
                +"  grammarAndUsageFeedback: [{grammarAndUsageError:string, correction:string, reason:string}],\n"
                +"  usefulPhrases: [string]";

        // 構造化リクエスト
        // ① リクエストボディ用のMapを準備
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        // ② ChatGPT に渡すメッセージリストを作成
        List<Map<String, String>> messages = new ArrayList<>();
        // - 最初に system ロールで「採点・解説モード」の指示を追加
        messages.add(Map.of("role", "system", "content", system));
        // ユーザーメッセージでタスク＋解答を渡す
        // ③ ユーザーからの回答情報をまとめたメッセージを作成
        Map<String, String> userMsg = new HashMap<>();
        // - "role":"user" でユーザー発話を示す
        userMsg.put("role", "user");
        // - buildUserPayload() で「問題文+解答」を一つの文字列にまとめる
        userMsg.put("content", buildUserPayload(task, answers, composition));
        messages.add(userMsg);
        // ④ body に messages をセット
        body.put("messages", messages);
        // ⑤ HTTP ヘッダーを準備
        HttpHeaders headers = new HttpHeaders();
        // - JSON 形式で送ることを指定
        headers.setContentType(MediaType.APPLICATION_JSON);
        // - Bearer トークン方式で API キーを渡す
        headers.setBearerAuth(apiKey);
        // ⑥ リクエストオブジェクトを作成
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        // ⑦ OpenAI API に POST リクエストを送り、レスポンスを Map で受け取る
        Map<?, ?> res = rest.postForObject(apiUrl, req, Map.class);

        // ⑧ レスポンスから必要な情報を取り出す
        Map<?, ?> choice = ((List<Map<?, ?>>) res.get("choices")).get(0);
        Map<?, ?> msg    = (Map<?, ?>) choice.get("message");
        String content   = (String) msg.get("content");
        
        // ⑨ JSON 文字列を DTO に変換して返す
        try {
            // ここで JSON 文字列 → FeedbackResultDto に変換して返す
            return objectMapper.readValue(content, FeedbackResultDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ChatGPT response into DTO", e);
        }
    }

    /**
     * JSON送信用ペイロードを組み立て
     * 
     * @param answers 選択肢の「A」「B」「C」「D」などの文字列リスト
     */
    private String buildUserPayload(ReadingTaskDto task, List<String> answers, String composition) {
        StringBuilder sb = new StringBuilder();
        sb.append("タスク情報:\n");
        sb.append("タイトル: ").append(task.getTitle()).append("\n");   // ←タイトルを追加
        sb.append("問題文: ").append(task.getPassage()).append("\n");
        for (int i = 0; i < task.getMcqs().size(); i++) {
            MCQDto m = task.getMcqs().get(i);
            // 各選択肢にラベルを付与（A:, B: ...）
            List<String> opts = m.getOptions();
            sb.append(i + 1).append(") ").append(m.getQuestion()).append("\n");
            char label = 'A';
            for (String opt : opts) {
                sb.append("   ").append(label).append(": ").append(opt).append("\n");
                label++;
            }
            // ユーザー回答も文字列そのまま
            sb.append("あなたの回答: ").append(answers.get(i)).append("\n");
        }
        sb.append("英作文: ").append(composition);
        return sb.toString();
    }
}