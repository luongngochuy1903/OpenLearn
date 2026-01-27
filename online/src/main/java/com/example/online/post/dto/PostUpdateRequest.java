package com.example.online.post.dto;

import com.example.online.document.dto.DocumentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    private String name;
    private String contentMarkdown;
    private List<DocumentRequestDTO> addDocs;
    private List<DocumentRequestDTO> removeDocs;
}
