package com.example.webcvtemplate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "video")
public class Video {
    @Id
    @Column(name = "video_code", length = 10)
    private String videoCode;

    @ManyToOne
    @JoinColumn(name = "lesson_code", nullable = false)
    private Lesson lesson;

    @Column(name = "video_title", nullable = false)
    private String videoTitle;

    @Column(name = "video_url", nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "has_humor", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean hasHumor = true;
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionVideo> questionVideos;

}