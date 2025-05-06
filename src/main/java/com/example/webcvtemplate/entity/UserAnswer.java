package com.example.webcvtemplate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_answer")
public class UserAnswer {
    @Id
    @Column(name = "answer_code", length = 10)
    private String answerCode;  // Để kiểu String cho mã câu trả lời

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_code", nullable = false)
    private QuestionVideo questionVideo;

    @Column(name = "selected_option", length = 1)
    private char selectedOption;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "answered_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime answeredAt;

    @PrePersist
    public void prePersist() {
        // Gọi hàm sinh mã câu trả lời trước khi lưu vào cơ sở dữ liệu
        this.answerCode = generateAnswerCode();
    }

    // Phương thức sinh mã câu trả lời tự động
    private String generateAnswerCode() {
        // Logic để sinh mã trả lời có dạng ANS0001, ANS0002, v.v.
        return "ANS" + String.format("%04d", (int) (Math.random() * 10000)); // Mã ngẫu nhiên 4 chữ số sau "ANS"
    }
}
