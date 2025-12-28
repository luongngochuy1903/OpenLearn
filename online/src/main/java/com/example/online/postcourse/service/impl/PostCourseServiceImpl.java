package com.example.online.postcourse.service.impl;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostCourse;
import com.example.online.domain.model.User;
import com.example.online.repository.PostCourseRepository;
import com.example.online.postcourse.service.PostCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<User> getRoleOfPost(Post post, ContributorRole role){
        List<PostCourse> postCourses = postCourseRepository.findUsersByPostAndRole(post, role);
        return postCourses.stream().map(postCourse -> postCourse.getUser()).toList();
    }

    public List<Course> getCourseByPost(Post post){
        List<PostCourse> postCourses = postCourseRepository.findCoursesByPost(post);
        return postCourses.stream().map(postCourse -> postCourse.getCourse()).toList();
    }

    public List<Post> getPostByUser(User user){
        List<PostCourse> postCourses = postCourseRepository.findPostsByUser(user);
        return postCourses.stream().map(postCourse -> postCourse.getPost()).toList();
    }
}
