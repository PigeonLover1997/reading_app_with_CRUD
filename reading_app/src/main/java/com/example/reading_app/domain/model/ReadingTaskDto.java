package com.example.reading_app.domain.model;

import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;

// 問題生成用のDTOクラス
public class ReadingTaskDto {
    private String passage; // 問題文
    private List<MCQDto> mcqs; // 選択式問題
    private String summaryPrompt; // 要約文の指示文
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

    // セッションにまだ task が無い初回アクセス時にも空の ReadingTaskDto が用意されるようにする
    @ModelAttribute("task")
    public ReadingTaskDto createEmptyTask() {
        return new ReadingTaskDto();
    }
}
