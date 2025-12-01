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
import com.example.online.service.CourseService;
import com.example.online.service.LessonService;
import com.example.online.service.ModuleService;
import com.example.online.service.PostService;
import com.example.online.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final LessonService lessonService;

    public String createPostWithCourse(PostCreateWithCourseRequest postCreateWithCourseRequest){
        var user = SecurityUtils.getCurrentUser();
        if (user == null){
            return null;
        }
        //Tạo và save post
        Post post = Post.builder()
                        .name(postCreateWithCourseRequest.getName()).contentURL(postCreateWithCourseRequest.getContentUrl())
                        .createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();

        List<Course> courses = postCreateWithCourseRequest.getCourseCreateRequests().stream()
                .map(courseReq -> {
                    //Create course
                    Course course = courseService.createCourse(courseReq);

                    //create Module
                    courseReq.getModuleCreateRequests().forEach(moduleReq -> {
                        Module module = moduleService.createModule(moduleReq);
                        //create lesson
                        moduleReq.getLessonCreateRequests().forEach(lessonReq -> {
                            Lesson lesson = lessonService.createLesson(lessonReq);
                            lesson.setModule(module);
                            module.getLessons().add(lesson);
                        });
                        CourseModule courseModule = CourseModule.builder()
                                        .user(user).module(module).course(course).role(ContributorRole.CREATOR).build();

                        module.getCourseModules().add(courseModule);
                        course.getCourseModules().add(courseModule);
                        user.getCourseModules().add(courseModule);
                    });
                    PostCourse postCourse = PostCourse.builder()
                            .user(user)
                            .post(post)
                            .course(course)
                            .role(ContributorRole.CREATOR)
                            .build();

                    post.getPostCourses().add(postCourse);
                    course.getPostCourses().add(postCourse);
                    user.getPostCourses().add(postCourse);
                    return course;
                }).toList();
        postRepository.save(post);
        return "Tạo post thành công";
    }

    public String createPost(PostCreateWithoutCourseRequest postCreateWithoutCourseRequest){
        return "Tạo post thành công";
    }
}
