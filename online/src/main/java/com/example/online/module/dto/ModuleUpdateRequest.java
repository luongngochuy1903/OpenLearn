package com.example.online.module.dto;

import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.lesson.dto.LessonUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleUpdateRequest {
    private String name;
    private String description;
    private List<LessonUpdateRequest> lessonUpdateRequests;
}
