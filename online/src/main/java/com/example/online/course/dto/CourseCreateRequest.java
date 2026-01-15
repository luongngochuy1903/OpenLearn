package com.example.online.course.dto;

import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.tag.dto.TagRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {
    @NotNull(message = "Course name cannot be empty")
    private String name;
    private String description;
    @NotNull(message = "Tag cannot be empty")
    private Set<TagRequest> tags;
    private LocalDateTime CreatedAt;
    private LocalDateTime updateAt;
    private List<ModuleCreateRequest> moduleCreateRequests;
}
