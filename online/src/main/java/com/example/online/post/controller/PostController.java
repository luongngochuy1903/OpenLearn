package com.example.online.post.controller;

import com.example.online.domain.model.Post;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.factory.PostCreateFactory;
import com.example.online.post.service.PostQueryService;
import com.example.online.post.service.PostService;
import com.example.online.postcourse.dto.PostCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostQueryService postQueryService;
    private final PostService postService;
    private final PostCreateFactory postCreateFactory;

    @PostMapping("/{types}")
    public ResponseEntity<PostCreateResponse> createPost(@PathVariable("types") PostCreateType postCreateType, @RequestBody PostCreateRequest postCreateRequest){
        Post post = postCreateFactory.create(postCreateType, postCreateRequest);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Create post successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCreateResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return ResponseEntity.ok("Delete post successfully");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostCreateResponse> updateMyPost(@PathVariable Long postId,
                                                           @RequestBody PostUpdateRequest postUpdateRequest){
        Post post = postService.updateMyPost(postId, postUpdateRequest);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Update post successfully").build();
        return ResponseEntity.ok(postCreateResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostGetResponse> getDetailPost(@PathVariable Long postId){
        return ResponseEntity.ok(postQueryService.viewPostDetail(postId));
    }
}
