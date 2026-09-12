package com.cards.flash.entities;

public class QuestionAnswer {
    private Integer id; // 1-based index inside the list
    private String question;
    private String answer;

    public QuestionAnswer() {}

    public QuestionAnswer(Integer id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}