package com.example.online.coursemodule.service.impl;

import com.example.online.domain.model.*;
import com.example.online.domain.model.Module;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
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

    public CourseModule createCourseModule(User user, Module module, Course course, ContributorRole role){
        CourseModule courseModule = CourseModule.builder()
                .user(user)
                .module(module)
                .course(course)
                .role(role)
                .build();

        module.getCourseModules().add(courseModule);
        course.getCourseModules().add(courseModule);
        user.getCourseModules().add(courseModule);

        save(courseModule);
        return courseModule;
    }

    public void deleteCourseModule(CourseModule courseModule){
        courseModuleRepository.delete(courseModule);
    }

    public List<User> getRoleOfCourse(Course course, ContributorRole role){
        List<CourseModule> courseModules = courseModuleRepository.findUsersByCourseAndRole(course, role);
        return courseModules.stream().map(courseModule -> courseModule.getUser())
                .filter(obj -> obj != null)
                .toList();
    }

    public List<Module> getModulesByCourse(Course course){
        List<CourseModule> courseModules = courseModuleRepository.findModulesByCourse(course);
        return courseModules.stream().map(courseModule -> courseModule.getModule())
                .filter(obj -> obj != null)
                .toList();
    }

    public boolean checkExistsByCourseAndModule(Long courseId, Long moduleId){
        return courseModuleRepository.existsByCourse_IdAndModule_Id(courseId, moduleId);
    }

    public List<Long> getCoursesIdByModule(Long moduleId){
        return courseModuleRepository.findCourseIdsByModuleId(moduleId)
                .stream().map(courseModule -> courseModule.getCourse().getId()).toList();
    }

    public boolean moduleExistsInAnyCourse(Long moduleId){
        return courseModuleRepository.existsByModuleId(moduleId);
    }

    public CourseModule findCourseModuleByCourseIdAndModuleId(Long courseId, Long moduleId){
        return courseModuleRepository.findByCourse_IdAndModule_Id(courseId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("This module is not attached to the course"));
    }
}
