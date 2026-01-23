package com.example.online.lesson.dto;

import com.example.online.document.dto.DocumentRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    @NotNull(message = "Lesson name cannot be empty")
    private String name;
    private String description;
    private List<DocumentRequestDTO> docs;
    @NotNull(message = "content cannot be empty")
    private String contentMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
