package com.example.reading_app.domain.model;

import java.util.List;

public class FeedbackResultDto {
    // 選択式問題の採点結果
    public static class McqResult {
        private int questionIndex;
        private boolean correct;
        private String explanation;
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
        public String getExplanation() {
            return explanation;
        }
        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    // 要約のスコア
    public static class SummaryScores {
        private int grammarScore;
        private int vocabScore;
        private int contentScore;
        // getter/setter
        public int getGrammarScore() {
            return grammarScore;
        }
        public void setGrammarScore(int grammarScore) {
            this.grammarScore = grammarScore;
        }
        public int getVocabScore() {
            return vocabScore;
        }

        public void setVocabScore(int vocabScore) {
            this.vocabScore = vocabScore;
        }
        public int getContentScore() {
            return contentScore;
        }
        public void setContentScore(int contentScore) {
            this.contentScore = contentScore;
        }
    }

    // 文法フィードバック（ミス箇所と修正案）
    public static class GrammarFeedback {
        private String grammarError;
        private String correction;
        private String reason;
        // getter/setter
        public String getGrammarError() {
            return grammarError;
        }
        public void setGrammarError(String grammarError) {
            this.grammarError = grammarError;
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

    // ↑３つの DTO をネストで定義しましたが、必要なら別ファイルでも OK

    private List<McqResult> mcqResults;
    private SummaryScores summaryScores;
    private List<GrammarFeedback> grammarFeedback;
    private List<String> usefulPhrases;
    private String highlightedPassage;  // 問題文のハイライト HTML
    private int passageWordCount;

    // getter/setter
    public List<McqResult> getMcqResults() {
        return mcqResults;
    }

    public SummaryScores getSummaryScores() {
        return summaryScores;
    }

    public List<GrammarFeedback> getGrammarFeedback() {
        return grammarFeedback;
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

    public void setSummaryScores(SummaryScores summaryScores) {
        this.summaryScores = summaryScores;
    }

    public void setGrammarFeedback(List<GrammarFeedback> grammarFeedback) {
        this.grammarFeedback = grammarFeedback;
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