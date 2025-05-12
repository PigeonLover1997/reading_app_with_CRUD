package com.example.reading_app.domain.model;

import java.util.List;

// 問題生成用のDTOクラス
public class ReadingTaskDto {
    private String passage;
    private List<MCQDto> mcqs;
    private String summaryPrompt;
    // getter / setter
    public String getPassage() {
        return passage;
    }
    public void setPassage(String passage) {
        this.passage = passage;
    }

    public List<MCQDto> getMcqs() {
        return mcqs;
    }

    public void setMcqs(List<MCQDto> mcqs) {
        this.mcqs = mcqs;
    }

    public String getSummaryPrompt() {
        return summaryPrompt;
    }

    public void setSummaryPrompt(String summaryPrompt) {
        this.summaryPrompt = summaryPrompt;
    }
}
