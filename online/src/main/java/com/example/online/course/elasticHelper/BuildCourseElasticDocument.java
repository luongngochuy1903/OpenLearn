package com.example.online.course.elasticHelper;

import com.example.online.course.dto.CourseGetResponse;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Module;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.module.dto.ModuleGetResponse;
import com.example.online.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuildCourseElasticDocument {
    private final CourseModuleService courseModuleService;
    private final CourseRepository courseRepository;
    private static final Logger LOG = LoggerFactory.getLogger(BuildCourseElasticDocument.class);

    public CourseGetResponse getCourseDocument(Long courseId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<Module> modules = courseModuleService.getModulesByCourse(course);
        List<ModuleGetResponse> moduleGetResponses = new ArrayList<>();
        if (modules != null) {
            moduleGetResponses = modules.stream().map(module ->
                    ModuleGetResponse.builder().moduleId(module.getId()).name(module.getName())
                            .description(module.getDescription()).build()
            ).toList();
        }
        Set<String> tags_name = course.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        User courseCreator = courseModuleService.getRoleOfCourse(course, ContributorRole.CREATOR).get(0);
        if (courseCreator.getLastName() == null || courseCreator.getFirstName() == null) {
            throw new ResourceNotFoundException("User name not found");
        }
        LOG.info("Built document for course {}", course.getId());
        return CourseGetResponse.builder().id(course.getId()).courseName(course.getName())
                .creatorName(courseCreator.getFirstName() + " " + courseCreator.getLastName()).creatorId(courseCreator.getId())
                .moduleGetResponse(moduleGetResponses)
                .description(course.getDescription()).tagName(tags_name).build();
    }
}
