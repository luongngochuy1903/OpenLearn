package com.example.online.course.service.impl;

import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.service.CourseService;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.*;
import com.example.online.domain.model.Module;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.lesson.dto.LessonGetResponse;
import com.example.online.lesson.service.LessonService;
import com.example.online.module.dto.ModuleGetResponse;
import com.example.online.module.service.ModuleService;
import com.example.online.repository.CourseRepository;
import com.example.online.tag.service.TagService;
import com.example.online.user.service.UserService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
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

    /*
        Function: Method returns details of course when people click the specifical course. Returning
        nested structure of Course entity to Lesson entity.
     */
    public CourseGetResponse viewCourseDetail(Long courseId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
        User courseCreator = courseModuleService.getRoleOfCourse(course, ContributorRole.CREATOR).get(0);
        if (courseCreator.getLastName() == null || courseCreator.getFirstName() == null){
            throw new ResourceNotFoundException("User name not found");
        }

        List<User> courseContributors = courseModuleService.getRoleOfCourse(course, ContributorRole.CONTRIBUTOR);

        List<String> courseContributorsName = courseContributors.stream().map(contributors -> contributors.getLastName() + " " + contributors.getFirstName()).toList();
        Set<String> tagName = course.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());

        List<Module> modules = courseModuleService.getModulesByCourse(course);

        List<ModuleGetResponse> moduleGetResponses = modules.stream().map(module -> {
            List<LessonGetResponse> lessonGetResponses = module.getLessons().stream().map(lesson -> {
                //Do not return lesson content for this response. Save for new href to redirect.
                return LessonGetResponse.builder().lessonId(lesson.getId()).name(lesson.getName()).description(lesson.getDescription())
                        .updateAt(lesson.getUpdateAt()).build();
            }).toList();

            return ModuleGetResponse.builder().moduleId(module.getId()).name(module.getName()).description(module.getDescription())
                    .updateAt(module.getUpdateAt()).lessonGetResponses(lessonGetResponses).build();
        }).toList();

        return CourseGetResponse.builder().id(course.getId()).courseName(course.getName())
                .description(course.getDescription()).tagName(tagName).creatorName(courseCreator.getLastName() + " " + courseCreator.getFirstName())
                .updateAt(course.getUpdateAt()).contributors(courseContributorsName).moduleGetResponse(moduleGetResponses).build();

    }
}