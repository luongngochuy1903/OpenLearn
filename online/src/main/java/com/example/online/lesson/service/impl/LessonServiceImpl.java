package com.example.online.lesson.service.impl;

import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.Lesson;
import com.example.online.domain.model.Module;
import com.example.online.lesson.dto.LessonUpdateRequest;
import com.example.online.repository.LessonRepository;
import com.example.online.repository.ModuleRepository;
import com.example.online.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public Lesson createLesson(LessonCreateRequest lessonCreateRequest){
        return Lesson.builder().name(lessonCreateRequest.getName()).description(lessonCreateRequest.getDescription())
                .contentURL(lessonCreateRequest.getContentUrl()).documentURL(lessonCreateRequest.getDocumentUrl())
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
        if (lessonUpdateRequest.getContentUrl() != null){
            lesson.setContentURL(lessonUpdateRequest.getContentUrl());
        }
        if (lessonUpdateRequest.getDocumentUrl() != null){
            lesson.setDocumentURL(lessonUpdateRequest.getDocumentUrl());
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
