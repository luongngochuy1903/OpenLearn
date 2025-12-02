package com.example.online.service.impl;

import com.example.online.DTO.CourseCreateRequest;
import com.example.online.model.Course;
import com.example.online.model.Tag;
import com.example.online.repository.CourseRepository;
import com.example.online.service.CourseService;
import com.example.online.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TagService tagService;

    public Course createCourse(CourseCreateRequest coursesReq){
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
        return course;
    }

    public void saveCourse(Course course){
        courseRepository.save(course);
    }
}
