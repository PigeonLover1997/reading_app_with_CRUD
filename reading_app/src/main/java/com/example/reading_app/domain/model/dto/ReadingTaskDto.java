package com.example.reading_app.domain.model.dto;

import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;

// 問題生成用のDTOクラス
public class ReadingTaskDto {
    private String title; // 表題を追加
    private String passage; // 問題文
    private String cefrLevel;  // 問題文のCEFRレベル
    private List<MCQDto> mcqs; // 選択式問題
    private String compositionPrompt; // 英作文の指示文
    // getter / setter

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public String getCefrLevel() {
        return cefrLevel;
    }
    public void setCefrLevel(String cefrLevel) {
        this.cefrLevel = cefrLevel;
    }
    
    public List<MCQDto> getMcqs() {
        return mcqs;
    }

    public void setMcqs(List<MCQDto> mcqs) {
        this.mcqs = mcqs;
    }

    public void setCompositionPrompt(String compositionPrompt) {
        this.compositionPrompt = compositionPrompt;
    }
        public String getCompositionPrompt() {
        return compositionPrompt;
    }

    // セッションにまだ task が無い初回アクセス時にも空の ReadingTaskDto が用意されるようにする
    @ModelAttribute("task")
    public ReadingTaskDto createEmptyTask() {
        return new ReadingTaskDto();
    }
}
