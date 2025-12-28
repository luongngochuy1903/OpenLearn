package com.example.online.user.controller;

import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.service.PostQueryService;
import com.example.online.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final PostQueryService postQueryService;
    @GetMapping("/posts")
    public ResponseEntity<List<PostGetResponse>> getMyPostDetail(){
        return ResponseEntity.ok(postQueryService.viewMyPostDetail());
    }
}
