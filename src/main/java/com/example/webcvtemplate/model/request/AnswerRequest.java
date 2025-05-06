package com.example.webcvtemplate.model.request;

public class AnswerRequest {
    private String questionIndex;  // Đổi từ int thành String
    private String answer;

    // Getter và Setter
    public String getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(String questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
