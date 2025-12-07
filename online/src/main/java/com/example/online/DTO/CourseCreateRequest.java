package com.example.online.DTO;

import com.example.online.model.Tag;
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
    @NotNull(message = "Module cannot be empty")
    private List<ModuleCreateRequest> moduleCreateRequests;
}
