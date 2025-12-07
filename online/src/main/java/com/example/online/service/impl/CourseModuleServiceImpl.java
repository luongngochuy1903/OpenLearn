package com.example.online.service.impl;

import com.example.online.enumerate.ContributorRole;
import com.example.online.model.Course;
import com.example.online.model.CourseModule;
import com.example.online.model.Module;
import com.example.online.model.User;
import com.example.online.repository.CourseModuleRepository;
import com.example.online.service.CourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
