package com.example.online.service.impl;

import com.example.online.DTO.PostCourseRequest;
import com.example.online.enumerate.ContributorRole;
import com.example.online.model.Course;
import com.example.online.model.Post;
import com.example.online.model.PostCourse;
import com.example.online.model.User;
import com.example.online.repository.PostCourseRepository;
import com.example.online.service.PostCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCourseServiceImpl implements PostCourseService {
    private final PostCourseRepository postCourseRepository;
    public PostCourse createPostCourse(Post post, Course course, User user){
        PostCourse postCourse = PostCourse.builder()
                .user(user)
                .post(post)
                .course(course)
                .role(ContributorRole.CREATOR)
                .build();

        post.getPostCourses().add(postCourse);
        course.getPostCourses().add(postCourse);
        user.getPostCourses().add(postCourse);
        save(postCourse);
        return postCourse;
    }

    public void save(PostCourse postCourse){
        postCourseRepository.save(postCourse);
    }
}
