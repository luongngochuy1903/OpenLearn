package com.example.online.post.service.impl;

import com.example.online.annotation.CheckCommunityMember;
import com.example.online.annotation.CheckCommunityPostForMember;
import com.example.online.course.service.CourseService;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.event.PostChangedEvent;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.service.PostCreateService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.CommunityRepository;
import com.example.online.repository.PostRepository;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service("PostWithCourseCreateServiceImpl")
@RequiredArgsConstructor
public class PostWithCourseCreateServiceImpl implements PostCreateService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final CourseService courseService;
    private final PostCourseService postCourseService;
    private final ApplicationEventPublisher publisher;

    @Override
    public PostCreateType getType() {
        return PostCreateType.WITH_COURSE;
    }

    /*
    Function: Create Post with Course attached
     */
    @Override
    @Transactional
    public Post createPost(PostCreateRequest postCreateRequest, User authUser) {
            if (authUser == null) {
                throw new UnauthorizedException("You need to login first");
            }
            User user = userService.findUserById(authUser.getId());

            // Tạo post
            Post post = Post.builder()
                    .name(postCreateRequest.getName())
                    .contentMarkdown(postCreateRequest.getContentMarkdown())
                    .creator(user)
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .postCourses(new HashSet<>())
                    .build();
            postRepository.save(post);

            for (var courseReq : postCreateRequest.getCourseCreateRequests()) {
                // Tạo course
                Course course = courseService.createCourse(courseReq, authUser);
                courseService.saveCourse(course);
                postCourseService.createPostCourse(post, course, user, ContributorRole.CREATOR);
            }
            System.out.println("Test coi post có trường gì: " + post.getPostCourses().size());
            publisher.publishEvent(new PostChangedEvent(post.getId()));
            return post;
    }

    @Override
    @Transactional
    @CheckCommunityMember(communityIdParam = "communityId", userParam = "authUser")
    public Post createPost(Long communityId, PostCreateRequest postCreateRequest, User authUser) {
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        User user = userService.findUserById(authUser.getId());

        Community community = communityRepository.findById(communityId).orElseThrow(() -> new ResourceNotFoundException("community not found"));

        // Tạo post
        Post post = Post.builder()
                .name(postCreateRequest.getName())
                .contentMarkdown(postCreateRequest.getContentMarkdown())
                .creator(user)
                .community(community)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        for (var courseReq : postCreateRequest.getCourseCreateRequests()) {
            // Tạo course
            Course course = courseService.createCourse(courseReq, authUser);
            courseService.saveCourse(course);
            postCourseService.createPostCourse(post, course, user, ContributorRole.CREATOR);
        }
        System.out.println("Test coi post có trường gì: " + post.getPostCourses().size());
        publisher.publishEvent(new PostChangedEvent(post.getId()));
        return post;
    }
}
