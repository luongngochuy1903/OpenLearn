package com.example.online.controller;

import com.example.online.DTO.PostCreateResponse;
import com.example.online.DTO.PostCreateWithCourseRequest;
import com.example.online.DTO.PostCreateWithoutCourseRequest;
import com.example.online.model.Post;
import com.example.online.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/api/v1/courses")
    public ResponseEntity<PostCreateResponse> createPostWithCourseController(@Valid @RequestBody PostCreateWithCourseRequest postCreateWithCourseRequest){
        Post post = postService.createPostWithCourse(postCreateWithCourseRequest);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Create post successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCreateResponse);
    }

    @PostMapping
    public ResponseEntity<PostCreateResponse> createPostWithoutCourseController(@Valid @RequestBody PostCreateWithoutCourseRequest postCreateWithoutCourseRequest){
        Post post = postService.createPost(postCreateWithoutCourseRequest);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Create post successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCreateResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return ResponseEntity.ok("Delete post successfully");
    }
}
