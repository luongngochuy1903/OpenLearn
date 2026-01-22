package com.example.online.post.dto;

import com.example.online.course.dto.CourseCreateRequest;
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
public class PostCreateRequest {
    @NotNull(message = "post name cannot be empty")
    private String name;
    private String contentMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private List<DocumentRequestDTO> docs;
    @NotNull(message = "Course cannot be empty")
    private List<CourseCreateRequest> courseCreateRequests;
}
