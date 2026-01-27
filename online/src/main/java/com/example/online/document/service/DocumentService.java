package com.example.online.document.service;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.domain.model.User;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.UploadType;

import java.util.List;

public interface DocumentService {
    DocumentOf getTypes();
    Object createDocument(Object owner, DocumentRequestDTO dto);
    Object createDocument(Object object, UploadType type, String url);
    Object updateDocument(Object object, DocumentRequestDTO dto);
    void deleteDocument(Object owner, String objectKey);
    void deleteDocument(Long id);
    List<?> resolveDocument(List<DocumentRequestDTO> docs, User user);
    }
