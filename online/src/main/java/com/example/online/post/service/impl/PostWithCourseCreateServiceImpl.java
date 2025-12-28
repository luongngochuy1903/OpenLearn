package com.example.online.post.service.impl;

import com.example.online.course.service.CourseService;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.service.PostCreateService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.PostRepository;
import com.example.online.user.service.UserService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service("PostWithCourseCreateServiceImpl")
@RequiredArgsConstructor
public class PostWithCourseCreateServiceImpl implements PostCreateService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final CourseService courseService;
    private final PostCourseService postCourseService;

    @Override
    public PostCreateType getType() {
        return PostCreateType.WITH_COURSE;
    }

    /*
    Function: Create Post with Course attached
     */
    @Override
    @Transactional
    public Post createPost(PostCreateRequest postCreateRequest) {
            var authUser = SecurityUtils.getCurrentUser();
            if (authUser == null) {
                throw new UnauthorizedException("You need to login first");
            }
            User user = userService.findUserById(authUser.getId());

            // Tạo post
            Post post = Post.builder()
                    .name(postCreateRequest.getName())
                    .contentMarkdown(postCreateRequest.getContentMarkdown())
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .postCourses(new HashSet<>())
                    .build();
            postRepository.save(post);

            for (var courseReq : postCreateRequest.getCourseCreateRequests()) {
                // Tạo course
                Course course = courseService.createCourse(courseReq);
                courseService.saveCourse(course);
                postCourseService.createPostCourse(post, course, user);
            }
            return post;
        }
}
