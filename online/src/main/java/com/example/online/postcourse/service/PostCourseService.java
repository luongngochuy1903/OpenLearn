package com.example.online.postcourse.service;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostCourse;
import com.example.online.domain.model.User;

import java.util.List;

public interface PostCourseService {
    PostCourse createPostCourse(Post post, Course course, User user);
    void save(PostCourse postCourse);
    List<User> getRoleOfPost(Post post, ContributorRole role);
    List<Course> getCourseByPost(Post post);
    List<Post> getPostByUser(User user);
    PostCourse findPostCourseByPostIdAndCourseId(Long postId, Long courseId);
    void deletePostCourse(PostCourse postCourse);
    boolean checkExistsByPostAndPost(Long postId, Long courseId);
}
