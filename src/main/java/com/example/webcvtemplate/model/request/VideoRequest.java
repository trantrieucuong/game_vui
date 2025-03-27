package com.example.webcvtemplate.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoRequest {
    private String lessonCode;
    private String videoTitle;
    private Integer durationMinutes;
    private Boolean hasHumor;
}
