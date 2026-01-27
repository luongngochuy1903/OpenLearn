package com.example.online.document.service.impl;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.*;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.UploadType;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.LessonDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonDocumentServiceImpl implements DocumentService {
    private final LessonDocumentRepository lessonDocumentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(LessonDocumentServiceImpl.class);

    public DocumentOf getTypes(){ return DocumentOf.LESSON; };

    @Override
    @Transactional
    public LessonDocument createDocument(Object object, DocumentRequestDTO dto){
        Lesson lesson = (Lesson) object;
        LessonDocument lessonDocument = LessonDocument.builder().lesson(lesson)
                .url(dto.getObjectKey()).type(dto.getType()).build();
        return lessonDocumentRepository.save(lessonDocument);
    }

    @Transactional
    public LessonDocument createDocument(Object object, UploadType type, String url){
        Lesson lesson = (Lesson) object;
        LessonDocument lessonDocument = LessonDocument.builder().lesson(lesson)
                .url(url).type(type).build();
        return lessonDocumentRepository.save(lessonDocument);
    }

    @Override
    public LessonDocument updateDocument(Object object, DocumentRequestDTO dto){
        LessonDocument lessonDocument = lessonDocumentRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        return lessonDocumentRepository.save(lessonDocument);
    }

    @Override
    public void deleteDocument(Object object, String objectKey){
        Lesson lesson = (Lesson) object;
        LessonDocument lessonDocument = lessonDocumentRepository.findByLessonAndUrl(lesson, objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in lesson"));
        lessonDocumentRepository.delete(lessonDocument);
    }

    public void deleteDocument(Long id){
        LessonDocument lessonDocument = lessonDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in user"));
        lessonDocumentRepository.delete(lessonDocument);
    }

    public List<LessonDocument> resolveDocument(List<DocumentRequestDTO> docs, User user) {
        LOG.info("Documents {} are created", docs);
        return docs.stream()
                .map(req ->
                        lessonDocumentRepository.findByUrl(req.getObjectKey())
                                .orElseGet(() -> lessonDocumentRepository.save(
                                        LessonDocument.builder().url(req.getObjectKey())
                                                .type(req.getType())
                                                .build()
                                ))
                )
                .toList();
    }
}
