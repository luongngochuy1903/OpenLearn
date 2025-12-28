package com.example.online.module.dto;

import com.example.online.lesson.dto.LessonCreateRequest;
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
public class ModuleCreateRequest {
    @NotNull(message = "Module name cannot be empty")
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    @NotNull(message = "Lesson name cannot be empty")
    private List<LessonCreateRequest> lessonCreateRequests;
}
