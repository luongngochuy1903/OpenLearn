package com.example.online.course.dto;

import com.example.online.tag.dto.TagRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {
    private String name;
    private String description;
    private Set<TagRequest> tags;
}
