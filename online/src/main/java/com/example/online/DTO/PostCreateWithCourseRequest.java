package com.example.online.DTO;

import com.example.online.model.Tag;
import com.example.online.repository.TagRepository;
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
public class PostCreateWithCourseRequest {
    @NotNull(message = "post name cannot be empty")
    private String name;
    private String contentMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    @NotNull(message = "Course cannot be empty")
    private List<CourseCreateRequest> courseCreateRequests;


}
