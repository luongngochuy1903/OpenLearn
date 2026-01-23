package com.example.online.lesson.service.impl;

import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.LessonDocument;
import com.example.online.enumerate.DocumentOf;
import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.Lesson;
import com.example.online.lesson.dto.LessonGetResponse;
import com.example.online.lesson.dto.LessonUpdateRequest;
import com.example.online.repository.LessonRepository;
import com.example.online.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final DocumentGenerateFactory documentGenerateFactory;

    @Transactional
    public Lesson createLesson(LessonCreateRequest lessonCreateRequest){

        Lesson lesson = Lesson.builder().name(lessonCreateRequest.getName()).description(lessonCreateRequest.getDescription())
                .contentMarkdown(lessonCreateRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();

        saveLesson(lesson);
        DocumentService documentService = documentGenerateFactory.getService(DocumentOf.LESSON);
        for (var documentReq : lessonCreateRequest.getDocs()) {
            documentService.createDocument(lesson, documentReq);
        }
        return lesson;
    }

    public Lesson updateLesson(LessonUpdateRequest lessonUpdateRequest){
        Lesson lesson = lessonRepository.findById(lessonUpdateRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (lessonUpdateRequest.getName() != null){
            lesson.setName(lessonUpdateRequest.getName());
        }
        if (lessonUpdateRequest.getDescription() != null){
            lesson.setDescription(lessonUpdateRequest.getDescription());
        }
        if (lessonUpdateRequest.getContentMarkdown() != null){
            lesson.setContentMarkdown(lessonUpdateRequest.getContentMarkdown());
        }
        if (lessonUpdateRequest.getDocs() != null){
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.LESSON);
            for (var documentReq : lessonUpdateRequest.getDocs()) {
                documentService.createDocument(lesson, documentReq);
            }
        }
        return saveLesson(lesson);
    }

    public Lesson saveLesson(Lesson lesson){
        return lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }

    // ========================= Query Lesson =====================
    public LessonGetResponse getLessonDetails(Long id){
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return LessonGetResponse.builder()
                .lessonId(lesson.getId())
                .contentMarkdown(lesson.getContentMarkdown())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .build();
    }
}
