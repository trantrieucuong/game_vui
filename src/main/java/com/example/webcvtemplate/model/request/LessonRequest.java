package com.example.webcvtemplate.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonRequest {
    private String lessonCode = UUID.randomUUID().toString();

    private String lessonTitle;

    private String description;

    private LocalDateTime createdAt;

    private boolean status;
}
