package com.example.online.course.service;

import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.domain.model.Course;

public interface CourseService {
    Course createCourse(CourseCreateRequest coursesReq);
    void deleteCourse(Long courseId);
    void saveCourse(Course course);
    CourseGetResponse viewCourseDetail(Long courseId);
}
