package com.example.online.service;

import com.example.online.model.Course;
import com.example.online.model.CourseModule;
import com.example.online.model.Module;
import com.example.online.model.User;

public interface CourseModuleService {
    void save (CourseModule courseModule);
    CourseModule createCourseModule(User user, Module module, Course course);
}
