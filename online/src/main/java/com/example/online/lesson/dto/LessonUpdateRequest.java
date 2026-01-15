package com.example.online.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private String documentUrl;
    private String contentUrl;
}
