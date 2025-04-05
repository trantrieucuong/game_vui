package com.example.webcvtemplate.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questionVideo")
public class QuestionVideo {
    @Id
    @Column(name = "question_code", length = 10)
    private String questionCode;

    @ManyToOne
    @JoinColumn(name = "video_code", nullable = false)
    private Video video;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "option_a", nullable = false, columnDefinition = "TEXT")
    private String optionA;

    @Column(name = "option_b", nullable = false, columnDefinition = "TEXT")
    private String optionB;

    @Column(name = "option_c", nullable = false, columnDefinition = "TEXT")
    private String optionC;

    @Column(name = "option_d", nullable = false, columnDefinition = "TEXT")
    private String optionD;

    @Column(name = "correct_option", length = 1, nullable = false)
    private char correctOption;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
}
