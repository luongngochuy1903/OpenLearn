package com.example.online.course.service;

import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.dto.CourseUpdateRequest;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.User;

public interface CourseService {
    Course createCourse(CourseCreateRequest coursesReq, User user);
    void deleteCourse(Long courseId, User user);
    void saveCourse(Course course);
    CourseGetResponse viewCourseDetail(Long courseId, User user);
    Course updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest, User user);
}
