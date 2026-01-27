package com.example.online.lesson.dto;

import com.example.online.document.dto.DocumentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private List<DocumentRequestDTO> addDocs;
    private List<DocumentRequestDTO> removeDocs;
    private String contentMarkdown;
}
