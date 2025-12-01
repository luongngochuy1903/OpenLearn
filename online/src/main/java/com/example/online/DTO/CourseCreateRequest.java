package com.example.online.DTO;

import com.example.online.model.Tag;
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
    private String name;
    private String description;
    private Set<TagRequest> tags;
    private LocalDateTime CreatedAt;
    private LocalDateTime updateAt;
    private List<ModuleCreateRequest> moduleCreateRequests;
}
