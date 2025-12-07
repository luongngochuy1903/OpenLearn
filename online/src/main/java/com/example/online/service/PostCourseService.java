package com.example.online.service;

import com.example.online.DTO.PostCourseRequest;
import com.example.online.model.Course;
import com.example.online.model.Post;
import com.example.online.model.PostCourse;
import com.example.online.model.User;

public interface PostCourseService {
    PostCourse createPostCourse(Post post, Course course, User user);
    void save(PostCourse postCourse);
}
