package com.example.online.service;

import com.example.online.DTO.PostCourseRequest;
import com.example.online.model.PostCourse;

public interface PostCourseService {
    PostCourse createPostCourse(PostCourseRequest postCourseRequest);
    void save(PostCourse postCourse);
}
