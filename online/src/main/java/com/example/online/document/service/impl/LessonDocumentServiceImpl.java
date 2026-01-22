package com.example.online.document.service.impl;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.Lesson;
import com.example.online.domain.model.LessonDocument;
import com.example.online.enumerate.DocumentOf;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.LessonDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonDocumentServiceImpl implements DocumentService {
    private final LessonDocumentRepository lessonDocumentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(LessonDocumentServiceImpl.class);

    public DocumentOf getTypes(){ return DocumentOf.LESSON; };

    @Override
    public LessonDocument createDocument(Object object, DocumentRequestDTO dto){
        Lesson lesson = (Lesson) object;
        LessonDocument lessonDocument = LessonDocument.builder().lesson(lesson)
                .url(dto.getObjectKey()).type(dto.getType()).build();
        lesson.getDocumentURL().add(lessonDocument);
        return lessonDocumentRepository.save(lessonDocument);
    }

    @Override
    public void deleteDocument(Object object, String objectKey){
        Lesson lesson = (Lesson) object;
        LessonDocument lessonDocument = lessonDocumentRepository.findByLessonAndUrl(lesson, objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in lesson"));
        lessonDocumentRepository.delete(lessonDocument);
    }

    public List<LessonDocument> resolveDocument(List<DocumentRequestDTO> docs) {
        LOG.info("Documents {} are created", docs);
        return docs.stream()
                .map(req ->
                        lessonDocumentRepository.findById(req.getId())
                                .orElseGet(() -> lessonDocumentRepository.save(
                                        LessonDocument.builder().url(req.getObjectKey())
                                                .type(req.getType())
                                                .build()
                                ))
                )
                .toList();
    }
}
