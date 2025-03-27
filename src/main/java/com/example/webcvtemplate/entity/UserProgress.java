package com.example.webcvtemplate.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_progress")
public class UserProgress {
    @Id
    @Column(name = "progress_code", length = 10)
    private String progressCode;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "lesson_code")
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "video_code")
    private Video video;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "progress_status", length = 50)
    private String progressStatus;
}
