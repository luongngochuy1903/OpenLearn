package com.example.online.service;

import com.example.online.DTO.LessonCreateRequest;
import com.example.online.model.Lesson;

public interface LessonService {
    Lesson createLesson(LessonCreateRequest lessonCreateRequest);
}
