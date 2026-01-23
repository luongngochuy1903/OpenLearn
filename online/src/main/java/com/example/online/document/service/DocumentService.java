package com.example.online.document.service;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.enumerate.DocumentOf;

import java.util.List;

public interface DocumentService {
    DocumentOf getTypes();
    Object createDocument(Object owner, DocumentRequestDTO dto);
    void deleteDocument(Object owner, String objectKey);
    List<?> resolveDocument(List<DocumentRequestDTO> docs);
    }
