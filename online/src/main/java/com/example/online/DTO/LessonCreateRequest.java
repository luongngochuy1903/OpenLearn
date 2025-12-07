package com.example.online.DTO;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Lesson name cannot be empty")
    private String name;
    private String description;
    private String documentUrl;
    @NotNull(message = "content cannot be empty")
    private String contentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
