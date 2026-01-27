package com.example.online.course.service.impl;

import com.example.online.annotation.CheckCourseCreator;
import com.example.online.course.dto.CourseCreateRequest;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.dto.CourseUpdateRequest;
import com.example.online.course.elasticHelper.BuildCourseElasticDocument;
import com.example.online.course.service.CourseService;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.*;
import com.example.online.domain.model.Module;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.enumerate.ContributorRole;
import com.example.online.event.CourseChangedEvent;
import com.example.online.event.CourseDeletedEvent;
import com.example.online.event.PostChangedEvent;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.helper.Indices;
import com.example.online.lesson.dto.LessonGetResponse;
import com.example.online.lesson.service.LessonService;
import com.example.online.module.dto.ModuleGetResponse;
import com.example.online.module.service.ModuleService;
import com.example.online.repository.CourseRepository;
import com.example.online.tag.service.TagService;
import com.example.online.user.service.UserService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
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
    private final CourseModuleService courseModuleService;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);


    @Transactional
    public Course createCourse(CourseCreateRequest coursesReq, User authUser){
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        User user = userService.findUserById(authUser.getId());

        Set<Tag> tagSet = tagService.resolveTags(coursesReq.getTags());

        Course course = Course.builder()
                .name(coursesReq.getName()).description(coursesReq.getDescription())
                .creator(user)
                .courseModules(new HashSet<>())
                .postCourses(new HashSet<>())
                .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now())
                .tags(tagSet)
                .build();

        saveCourse(course);
        if (!coursesReq.getModuleCreateRequests().isEmpty()) {
            for (var moduleReq : coursesReq.getModuleCreateRequests()) {
                Module module = moduleService.createModule(moduleReq, user);
                courseModuleService.createCourseModule(user, module, course, ContributorRole.CREATOR);
            }
        }
        else {
            courseModuleService.createCourseModule(user, null, course, ContributorRole.CREATOR);
        }
        System.out.println("Test coi course có trường gì: " + course.getCourseModules().size());
        LOG.info("User {} created course id {} - {}", user.getEmail(), course.getId(), course.getName());
        publisher.publishEvent(new CourseChangedEvent(course.getId()));
        return course;
    }

    public void saveCourse(Course course){
        courseRepository.save(course);
    }

    @Transactional
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "authUser")
    public Course updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest, User authUser){
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (courseUpdateRequest.getName() != null){
            course.setName(courseUpdateRequest.getName());
        }
        if (courseUpdateRequest.getDescription() != null){
            course.setDescription(courseUpdateRequest.getDescription());
        }
        Set<Tag> tagSet = tagService.resolveTags(courseUpdateRequest.getTags());
        course.setTags(tagSet);
        LOG.info("User {} updated course id {} - {}", authUser.getEmail(), course.getId(), course.getName());
        publisher.publishEvent(new CourseChangedEvent(course.getId()));
        return courseRepository.save(course);
    }

    @Transactional
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "authUser")
    public void deleteCourse(Long courseId, User authUser){
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found !"));

        courseRepository.delete(course);
        LOG.info("User {} deleted course id {} - {}", authUser.getEmail(), course.getId(), course.getName());
        publisher.publishEvent(new CourseDeletedEvent(course.getId()));
    }

    /*
        Function: Method returns details of course when people click the specifical course. Returning
        nested structure of Course entity to Lesson entity.
     */
    public CourseGetResponse viewCourseDetail(Long courseId, User authUser){
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found!"));
        User courseCreator = course.getCreator();
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

        LOG.info("User {} call view course detail id {} - {}", authUser.getEmail(), course.getId(), course.getName());

        return CourseGetResponse.builder().id(course.getId()).courseName(course.getName())
                .description(course.getDescription()).tagName(tagName).creatorName(courseCreator.getLastName() + " " + courseCreator.getFirstName())
                .updateAt(course.getUpdateAt()).contributors(courseContributorsName).moduleGetResponse(moduleGetResponses).build();
    }
}