package com.example.online.service;

import com.example.online.DTO.CourseCreateRequest;
import com.example.online.model.Course;

public interface CourseService {
    Course createCourse(CourseCreateRequest coursesReq);
}
