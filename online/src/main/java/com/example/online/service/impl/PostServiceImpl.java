package com.example.online.service.impl;

import com.example.online.DTO.PostCreateResponse;
import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.DTO.PostCreateWithoutCourseRequest;
import com.example.online.enumerate.ContributorRole;
import com.example.online.model.*;
import com.example.online.model.Module;
import com.example.online.repository.CourseRepository;
import com.example.online.repository.LessonRepository;
import com.example.online.repository.PostRepository;
import com.example.online.repository.UserRepository;
import com.example.online.service.*;
import com.example.online.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final CourseModuleService courseModuleService;
    private final PostCourseService postCourseService;

    @Transactional
    public String createPostWithCourse(PostCreateWithCourseRequest postCreateWithCourseRequest){
        var authUser = SecurityUtils.getCurrentUser();
        if (authUser == null) return null;
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo post
        Post post = Post.builder()
                .name(postCreateWithCourseRequest.getName())
                .contentURL(postCreateWithCourseRequest.getContentUrl())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        for (var courseReq : postCreateWithCourseRequest.getCourseCreateRequests()) {
            // Tạo course
            Course course = courseService.createCourse(courseReq);
            courseService.saveCourse(course);

            for (var moduleReq : courseReq.getModuleCreateRequests()) {
                Module module = moduleService.createModule(moduleReq);
                moduleService.saveModule(module);

                for (var lessonReq : moduleReq.getLessonCreateRequests()) {
                    Lesson lesson = lessonService.createLesson(lessonReq);
                    lesson.setModule(module);
                    lessonService.saveLesson(lesson);
//                    module.getLessons().add(lesson);
                }

                CourseModule courseModule = CourseModule.builder()
                        .user(user)
                        .module(module)
                        .course(course)
                        .role(ContributorRole.CREATOR)
                        .build();

                module.getCourseModules().add(courseModule);
                course.getCourseModules().add(courseModule);
                user.getCourseModules().add(courseModule);
                courseModuleService.save(courseModule);

            }

            PostCourse postCourse = PostCourse.builder()
                    .user(user)
                    .post(post)
                    .course(course)
                    .role(ContributorRole.CREATOR)
                    .build();

            post.getPostCourses().add(postCourse);
            course.getPostCourses().add(postCourse);
            user.getPostCourses().add(postCourse);
            postCourseService.save(postCourse);
        }
        return "Tạo post thành công";
    }

    public String createPost(PostCreateWithoutCourseRequest postCreateWithoutCourseRequest){
        return "Tạo post thành công";
    }
}
