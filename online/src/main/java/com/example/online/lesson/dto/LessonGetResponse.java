package com.example.online.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonGetResponse {
    private Long lessonId;
    private String name;
    private String description;
    private String documentURL;
    private String contentURL;
    private LocalDateTime updateAt;

}
