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
public class PostCreateWithoutCourseRequest {
    private String name;
    private String contentMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
