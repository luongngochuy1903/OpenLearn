package com.example.online.lesson.service;

import com.example.online.domain.model.Module;
import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.domain.model.Lesson;
import com.example.online.lesson.dto.LessonGetResponse;
import com.example.online.lesson.dto.LessonUpdateRequest;

public interface LessonService {
    Lesson createLesson(LessonCreateRequest lessonCreateRequest, Module module);
    Lesson updateLesson(LessonUpdateRequest lessonUpdateRequest);
    Lesson saveLesson(Lesson lesson);
    void deleteLesson(Long lessonId);
    LessonGetResponse getLessonDetails(Long id);
}
