package com.example.online.coursemodule.service.impl;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.CourseModule;
import com.example.online.domain.model.Module;
import com.example.online.domain.model.User;
import com.example.online.repository.CourseModuleRepository;
import com.example.online.coursemodule.service.CourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseModuleServiceImpl implements CourseModuleService {
    private final CourseModuleRepository courseModuleRepository;
    public void save (CourseModule courseModule){
        courseModuleRepository.save(courseModule);
    }

    public CourseModule createCourseModule(User user, Module module, Course course){
        CourseModule courseModule = CourseModule.builder()
                .user(user)
                .module(module)
                .course(course)
                .role(ContributorRole.CREATOR)
                .build();

        module.getCourseModules().add(courseModule);
        course.getCourseModules().add(courseModule);
        user.getCourseModules().add(courseModule);

        save(courseModule);
        return courseModule;
    }

    public List<User> getRoleOfCourse(Course course, ContributorRole role){
        List<CourseModule> courseModules = courseModuleRepository.findUsersByCourseAndRole(course, role);
        return courseModules.stream().map(courseModule -> courseModule.getUser()).toList();
    }

    public List<Module> getModulesByCourse(Course course){
        List<CourseModule> courseModules = courseModuleRepository.findModulesByCourse(course);
        return courseModules.stream().map(courseModule -> courseModule.getModule()).toList();
    }

}
