package com.example.online.service;

import com.example.online.DTO.LessonCreateRequest;
import com.example.online.model.Lesson;

public interface LessonService {
    Lesson createLesson(LessonCreateRequest lessonCreateRequest);
    Lesson createLesson(Long moduleId, LessonCreateRequest lessonCreateRequest);
    void saveLesson(Lesson lesson);
    void deleteLesson(Long lessonId);
}
