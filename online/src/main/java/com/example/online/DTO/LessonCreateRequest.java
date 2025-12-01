package com.example.online.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    private String name;
    private String description;
    private String documentUrl;
    private String contentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
