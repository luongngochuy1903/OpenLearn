package com.example.online.course.controller;

import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseCreateResponse;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.domain.model.Course;
import com.example.online.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseCreateResponse> createCourses(@Valid @RequestBody CourseCreateRequest courseCreateRequest){
        Course course = courseService.createCourse(courseCreateRequest);
        CourseCreateResponse courseCreateResponse = CourseCreateResponse.builder().courseId(course.getId()).message("Create course successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(courseCreateResponse);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourses(@PathVariable Long courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok("Delete course successfully");
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseGetResponse> getDetailCourse(@PathVariable Long courseId){
        return ResponseEntity.ok(courseService.viewCourseDetail(courseId));
    }
}
