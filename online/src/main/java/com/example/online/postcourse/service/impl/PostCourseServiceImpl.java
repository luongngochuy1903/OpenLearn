package com.example.online.postcourse.service.impl;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostCourse;
import com.example.online.domain.model.User;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.PostCourseRepository;
import com.example.online.postcourse.service.PostCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCourseServiceImpl implements PostCourseService {
    private final PostCourseRepository postCourseRepository;
    public PostCourse createPostCourse(Post post, Course course, User user, ContributorRole role){
        PostCourse postCourse = PostCourse.builder()
                .user(user)
                .post(post)
                .course(course)
                .role(role)
                .build();

        post.getPostCourses().add(postCourse);
        course.getPostCourses().add(postCourse);
        user.getPostCourses().add(postCourse);
        save(postCourse);
        return postCourse;
    }

    public boolean checkExistsByPostAndCourse(Long postId, Long courseId){
        return postCourseRepository.existsByPost_IdAndCourse_Id(postId, courseId);
    }

    public PostCourse findPostCourseByPostIdAndCourseId(Long postId, Long courseId){
        return postCourseRepository.findByPost_IdAndCourse_Id(postId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("This course is not attached to the post"));
    }

    public void deletePostCourse(PostCourse postCourse){
        postCourseRepository.delete(postCourse);
    }

    public void save(PostCourse postCourse){
        postCourseRepository.save(postCourse);
    }

    public List<User> getRoleOfPost(Post post, ContributorRole role){
        List<PostCourse> postCourses = postCourseRepository.findByPostAndRole(post, role);
        return postCourses.stream().map(postCourse -> postCourse.getUser())
                .filter(obj -> obj != null)
                .toList();
    }

    public List<Course> getCourseByPost(Post post){
        List<PostCourse> postCourses = postCourseRepository.findByPost(post);
        return postCourses.stream().map(postCourse -> postCourse.getCourse())
                .filter(obj -> obj != null)
                .toList();
    }

    public List<Post> getPostByUser(User user){
        List<PostCourse> postCourses = postCourseRepository.findByUser(user);
        return postCourses.stream().map(postCourse -> postCourse.getPost())
                .filter(obj -> obj != null)
                .toList();
    }
}
