package com.example.online.service;

import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.DTO.PostCreateWithoutCourseRequest;

public interface PostService {
    String createPostWithCourse(PostCreateWithCourseRequest postCreateWithCourseRequest);
    String createPost(PostCreateWithoutCourseRequest postCreateWithoutCourseRequest);
}
