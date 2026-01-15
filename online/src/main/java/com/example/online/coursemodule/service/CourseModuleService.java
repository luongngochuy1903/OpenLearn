package com.example.online.coursemodule.service;

import com.example.online.domain.model.Course;
import com.example.online.domain.model.CourseModule;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Module;

import java.util.List;

public interface CourseModuleService {
    void save (CourseModule courseModule);
    CourseModule createCourseModule(User user, Module module, Course course);
    List<User> getRoleOfCourse(Course course, ContributorRole role);
    List<Module> getModulesByCourse(Course course);
    List<Long> getCoursesIdByModule(Long moduleId);
    boolean moduleExistsInAnyCourse(Long moduleId);
}
