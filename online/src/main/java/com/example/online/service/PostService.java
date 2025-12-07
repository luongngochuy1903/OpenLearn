package com.example.online.service;

import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.DTO.PostCreateWithoutCourseRequest;
import com.example.online.model.Post;

public interface PostService {
    Post createPostWithCourse(PostCreateWithCourseRequest postCreateWithCourseRequest);
    Post createPost(PostCreateWithoutCourseRequest postCreateWithoutCourseRequest);
    void deletePost(Long postId);
}
