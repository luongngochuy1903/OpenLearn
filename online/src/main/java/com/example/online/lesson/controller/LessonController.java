package com.example.online.lesson.controller;

import com.example.online.lesson.dto.LessonGetResponse;
import com.example.online.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonGetResponse> getLesson(@PathVariable Long lessonId){
        return ResponseEntity.ok(lessonService.getLessonDetails(lessonId));
    }
}
