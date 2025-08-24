package com.example.reading_app.domain.model.dto;

import java.util.List;

// DTO：画面やAPIとのデータの受け渡し専用の入れ物となるクラス
public class FeedbackResultDto {
    // 選択式問題の採点結果を保持する内部クラス
    public static class McqResult {
        private int questionIndex;
        private boolean correct;
        private String evidence; // ← 本文中の該当箇所（引用＋和訳）
        private String explanation; // ← 理由や解説（なぜ正解か・誤答解説など）
        // getter/setter

        public int getQuestionIndex() {
            return questionIndex;
        }

        public void setQuestionIndex(int questionIndex) {
            this.questionIndex = questionIndex;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }

        public String getEvidence() {
            return evidence;
        }

        public void setEvidence(String evidence) {
            this.evidence = evidence;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    // 英作文のスコアと講評を保持する内部クラス
    public static class CompositionScores {
        private int grammarAndUsageScore;
        private int contentScore;
        // ScoreCommentary は、スコアに応じた講評
        private String scoreCommentary;

        // getter/setter
        public int getGrammarAndUsageScore() {
            return grammarAndUsageScore;
        }

        public void setGrammarAndUsageScore(int grammarAndUsageScore) {
            this.grammarAndUsageScore = grammarAndUsageScore;
        }

        public int getContentScore() {
            return contentScore;
        }

        public void setContentScore(int contentScore) {
            this.contentScore = contentScore;
        }   
        public String getScoreCommentary() {
            return scoreCommentary;
        }
        public void setScoreCommentary(String scoreCommentary) {
            this.scoreCommentary = scoreCommentary;
        }
    }

    // 文法・語法フィードバック（ミス箇所と修正案と理由）を保持する内部クラス
    public static class GrammarAndUsageFeedback {
        private String grammarAndUsageError;
        private String correction;
        private String reason;

        // getter/setter
        public String getGrammarAndUsageError() {
            return grammarAndUsageError;
        }

        public void setGrammarAndUsageError(String grammarAndUsageError) {
            this.grammarAndUsageError = grammarAndUsageError;
        }

        public String getCorrection() {
            return correction;
        }

        public void setCorrection(String correction) {
            this.correction = correction;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    private List<McqResult> mcqResults; // 選択式問題の採点結果一覧
    private CompositionScores compositionScores; // 英作文のスコアと講評
    private List<GrammarAndUsageFeedback> grammarAndUsageFeedback; // 文法・語法フィードバック一覧
    // 上記は上述の内部クラスの形で定義されている
    private List<String> usefulPhrases; // 有用なフレーズ一覧
    private String highlightedPassage; // 問題文の中でハイライトしたHTML
    private int passageWordCount; // 問題文の語数

    // getter/setter
    public List<McqResult> getMcqResults() {
        return mcqResults;
    }

    public CompositionScores getCompositionScores() {
        return compositionScores;
    }

    public List<GrammarAndUsageFeedback> getGrammarAndUsageFeedback() {
        return grammarAndUsageFeedback;
    }

    public List<String> getUsefulPhrases() {
        return usefulPhrases;
    }

    public String getHighlightedPassage() {
        return highlightedPassage;
    }

    public int getPassageWordCount() {
        return passageWordCount;
    }

    public void setMcqResults(List<McqResult> mcqResults) {
        this.mcqResults = mcqResults;
    }

    public void setCompositionScores(CompositionScores compositionScores) {
        this.compositionScores = compositionScores;
    }

    public void setGrammarAndUsageFeedback(List<GrammarAndUsageFeedback> grammarAndUsageFeedback) {
        this.grammarAndUsageFeedback = grammarAndUsageFeedback;
    }

    public void setUsefulPhrases(List<String> usefulPhrases) {
        this.usefulPhrases = usefulPhrases;
    }

    public void setHighlightedPassage(String highlightedPassage) {
        this.highlightedPassage = highlightedPassage;
    }

    public void setPassageWordCount(int passageWordCount) {
        this.passageWordCount = passageWordCount;
    }
}