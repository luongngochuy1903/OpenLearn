package com.example.online.service.impl;

import com.example.online.DTO.LessonCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.model.Lesson;
import com.example.online.model.Module;
import com.example.online.repository.LessonRepository;
import com.example.online.repository.ModuleRepository;
import com.example.online.service.LessonService;
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


    public Lesson createLesson(Long moduleId, LessonCreateRequest lessonCreateRequest){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        return Lesson.builder().name(lessonCreateRequest.getName()).description(lessonCreateRequest.getDescription())
                .module(module)
                .contentURL(lessonCreateRequest.getContentUrl()).documentURL(lessonCreateRequest.getDocumentUrl())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();
    }

    public void saveLesson(Lesson lesson){
        lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }
}
