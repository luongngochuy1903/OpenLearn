package com.example.online.course.controller;

import com.example.online.annotation.CurrentUser;
import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseCreateResponse;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.dto.CourseUpdateRequest;
import com.example.online.domain.model.Course;
import com.example.online.course.service.CourseService;
import com.example.online.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    @PostMapping
    public ResponseEntity<CourseCreateResponse> createCourses(@Valid @RequestBody CourseCreateRequest courseCreateRequest,
                                                              @CurrentUser User user){
        LOG.info("POST /api/v1/courses - Body: CourseCreateRequest");
        Course course = courseService.createCourse(courseCreateRequest, user);
        CourseCreateResponse courseCreateResponse = CourseCreateResponse.builder().courseId(course.getId()).message("Create course successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(courseCreateResponse);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourses(@PathVariable Long courseId, @CurrentUser User user){
        LOG.info("DELETE /api/v1/courses/{} - Body: Null", courseId);
        courseService.deleteCourse(courseId, user);
        return ResponseEntity.ok("Delete course successfully");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseCreateResponse> updateCourses(@PathVariable Long courseId, @RequestBody CourseUpdateRequest courseUpdateRequest,
                                                              @CurrentUser User user){
        Course course = courseService.updateCourse(courseId, courseUpdateRequest, user);
        CourseCreateResponse courseCreateResponse = CourseCreateResponse.builder().courseId(course.getId()).message("Update course successfully").build();
        return ResponseEntity.ok(courseCreateResponse);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseGetResponse> getDetailCourse(@PathVariable Long courseId, @CurrentUser User user){
        LOG.info("GET /api/v1/courses/{} - Body: Null", courseId);
        return ResponseEntity.ok(courseService.viewCourseDetail(courseId, user));
    }
}
