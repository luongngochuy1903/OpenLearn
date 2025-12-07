package com.example.online.service.impl;

import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.DTO.PostCreateWithoutCourseRequest;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.model.*;
import com.example.online.model.Module;
import com.example.online.repository.PostRepository;
import com.example.online.repository.UserRepository;
import com.example.online.service.*;
import com.example.online.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final LessonService lessonService;
    private final CourseModuleService courseModuleService;
    private final PostCourseService postCourseService;
    private final UserService userService;

    @Transactional
    public Post createPostWithCourse(PostCreateWithCourseRequest postCreateWithCourseRequest){
        var authUser = SecurityUtils.getCurrentUser();
        if (authUser == null) return null;
        User user = userService.findUserById(authUser.getId());

        // Tạo post
        Post post = Post.builder()
                .name(postCreateWithCourseRequest.getName())
                .contentMarkdown(postCreateWithCourseRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        for (var courseReq : postCreateWithCourseRequest.getCourseCreateRequests()) {
            // Tạo course
            Course course = courseService.createCourse(courseReq);
            courseService.saveCourse(course);
            postCourseService.createPostCourse(post, course, user);
        }
        return post;
    }

    @Transactional
    public Post createPost(PostCreateWithoutCourseRequest postCreateWithoutCourseRequest){
        var authUser = SecurityUtils.getCurrentUser();
        if (authUser == null) return null;
        User user = userService.findUserById(authUser.getId());

        Post post = Post.builder()
                .name(postCreateWithoutCourseRequest.getName())
                .contentMarkdown(postCreateWithoutCourseRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        PostCourse postCourse = PostCourse.builder()
                .user(user)
                .post(post)
                .role(ContributorRole.CREATOR)
                .build();
        postCourseService.save(postCourse);
        return post;
    }

    public void deletePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found !"));
        postRepository.delete(post);
        //Thêm xóa comment related
    }
}
