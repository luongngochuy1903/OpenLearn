package com.example.online.service.impl;

import com.example.online.DTO.CourseCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.model.*;
import com.example.online.model.Module;
import com.example.online.repository.CourseRepository;
import com.example.online.service.*;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TagService tagService;
    private final ModuleService moduleService;
    private final LessonService lessonService;
    private final CourseModuleService courseModuleService;
    private final UserService userService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Course createCourse(CourseCreateRequest coursesReq){
        var authUser = SecurityUtils.getCurrentUser();
        if (authUser == null) return null;
        User user = userService.findUserById(authUser.getId());

        Set<Tag> tagSet = coursesReq.getTags().stream()
                .map(tagDTO -> Tag.builder().name(tagDTO.getName()).build())
                .collect(Collectors.toSet());
        for(Tag tag : tagSet){
            tagService.save(tag);
        }

        Course course = Course.builder()
                .name(coursesReq.getName()).description(coursesReq.getDescription())
                .courseModules(new HashSet<>())
                .postCourses(new HashSet<>())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now())
                .tags(tagSet)
                .build();

        saveCourse(course);

        for (var moduleReq : coursesReq.getModuleCreateRequests()){
            Module module = moduleService.createModule(moduleReq);
            moduleService.saveModule(module);

            for (var lessonReq : moduleReq.getLessonCreateRequests()){
                Lesson lesson = lessonService.createLesson(lessonReq);
                lesson.setModule(module);
                lessonService.saveLesson(lesson);
            }

            CourseModule courseModule = courseModuleService.createCourseModule(user, module, course);

        }
        return course;
    }

    public void saveCourse(Course course){
        courseRepository.save(course);
    }

    public void deleteCourse(Long courseId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found !"));
    }
}
