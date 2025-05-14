package com.example.reading_app.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.reading_app.domain.model.MCQDto;
import com.example.reading_app.domain.model.ReadingTaskDto;

import java.util.*;

@Service
public class FeedbackGenerationService {

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
     * @param task    元のTaskオブジェクト（passage, mcqs, summaryPrompt）
     * @param answers 選択式回答のリスト (選択肢インデックス)
     * @param summary 要約文の自由入力
     * @return JSON文字列のまま返却し、Controllerにてパース
     */
    public String gradeAndFeedback(ReadingTaskDto task, List<String> answers, String summary) {
        // システム指示
        String system = "You are an expert English teacher. " 
                +"First, grade each multiple-choice answer: for each, provide whether it's correct or not, and highlight the exact part in the passage as justification. "
                +"Then, evaluate the summary on three criteria (grammar, vocabulary range, content) on a 0-10 scale. 0: Not answered, 1-2: Poor, 3-4: Below Average, 5-6: Average, 7-8: Good, 9-10: Excellent. "
                +"Also point out errors in grammar and usage and suggest corrections, and finally show a couple of useful expressions from the passage that are useful for the student to use in writing or speaking, with Japanese explanation for each phrase. "
                +"Respond entirely in Japanese, and output JSON with keys:\n" 
                +"  mcqResults: [{questionIndex:int, correct:boolean, explanation:string}],\n" 
                +"  summaryScores: {grammarScore:int, vocabScore:int, contentScore:int},\n" 
                +"  grammarFeedback: [{grammarError:string, correction:string}],\n" 
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
        userMsg.put("content", buildUserPayload(task, answers, summary));
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
        // ⑧ レスポンスから choices 配列を取得し、先頭要素を取り出す
        Map<?, ?> choice = ((List<Map<?, ?>>) res.get("choices")).get(0);
        // ⑨ 先頭要素の "message" フィールドから "content" を取り出す
        Map<?, ?> msg = (Map<?, ?>) choice.get("message");
        // ⑩ 生成されたフィードバック文章を呼び出し元に返す
        return (String) msg.get("content");
    }

    /**
     * JSON送信用ペイロードを組み立て
     * 
     * @param answers 選択肢の「A」「B」「C」「D」などの文字列リスト
     */
    private String buildUserPayload(ReadingTaskDto task, List<String> answers, String summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("タスク情報:\n");
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
        sb.append("要約: ").append(summary);
        return sb.toString();
    }
}