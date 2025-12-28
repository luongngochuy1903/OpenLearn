package com.example.online.lesson.controller;

import com.example.online.lesson.dto.LessonCreateRequest;
import com.example.online.lesson.dto.LessonCreateResponse;
import com.example.online.domain.model.Lesson;
import com.example.online.lesson.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/{moduleId}/lessons")
    public ResponseEntity<LessonCreateResponse> createLessonInModule(@Valid @RequestBody LessonCreateRequest lessonCreateRequest,
                                                                @PathVariable Long moduleId){
        Lesson lesson = lessonService.createLesson(moduleId, lessonCreateRequest);
        LessonCreateResponse lessonCreateResponse = LessonCreateResponse.builder().lessonId(lesson.getId())
                .message("Create lesson successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonCreateResponse);
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long lessonId){
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok("Delete lesson successfully");
    }
}
