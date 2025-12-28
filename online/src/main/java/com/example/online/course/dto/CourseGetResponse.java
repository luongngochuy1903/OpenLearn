package com.example.online.course.dto;

import com.example.online.module.dto.ModuleGetResponse;
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
public class CourseGetResponse {
    private Long id;
    private String courseName;
    private String description;
    private Set<String> tagName;
    private LocalDateTime updateAt;
    private List<ModuleGetResponse> moduleGetResponse;
    private String creatorName;
    private Long creatorId;
    private List<String> contributors;
}
