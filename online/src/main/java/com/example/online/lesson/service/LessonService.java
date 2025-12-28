package com.example.online.lesson.service;

import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.domain.model.Lesson;

public interface LessonService {
    Lesson createLesson(LessonCreateRequest lessonCreateRequest);
    Lesson createLesson(Long moduleId, LessonCreateRequest lessonCreateRequest);
    void saveLesson(Lesson lesson);
    void deleteLesson(Long lessonId);
}
