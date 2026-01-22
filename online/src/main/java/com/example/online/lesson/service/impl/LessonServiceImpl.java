package com.example.online.lesson.service.impl;

import com.example.online.domain.model.LessonDocument;
import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.Lesson;
import com.example.online.lesson.dto.LessonUpdateRequest;
import com.example.online.repository.LessonRepository;
import com.example.online.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    public Lesson createLesson(LessonCreateRequest lessonCreateRequest){
        return Lesson.builder().name(lessonCreateRequest.getName()).description(lessonCreateRequest.getDescription())
                .contentMarkdown(lessonCreateRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();
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
        if (lessonUpdateRequest.getObjectKey() != null){
            lesson.setDocumentURL(lessonUpdateRequest.getObjectKey());
        }
        return saveLesson(lesson);
    }

    public Lesson saveLesson(Lesson lesson){
        return lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }
}
