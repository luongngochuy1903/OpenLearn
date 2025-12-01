package com.example.online.service.impl;

import com.example.online.DTO.LessonCreateRequest;
import com.example.online.model.Lesson;
import com.example.online.repository.LessonRepository;
import com.example.online.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    public Lesson createLesson(LessonCreateRequest lessonCreateRequest){
        return Lesson.builder().name(lessonCreateRequest.getName()).description(lessonCreateRequest.getDescription())
                .contentURL(lessonCreateRequest.getContentUrl()).documentURL(lessonCreateRequest.getDocumentUrl())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();
    }
}
