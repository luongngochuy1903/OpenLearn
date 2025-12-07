package com.example.online.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<ModuleGetResponse> moduleGetResponse;
}
