package com.example.online.controller;

import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public String createPostWithCourseController(@RequestBody PostCreateWithCourseRequest postCreateWithCourseRequest){
        return postService.createPostWithCourse(postCreateWithCourseRequest);
    }
}
