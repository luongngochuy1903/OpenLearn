package com.example.online.post.service;

import com.example.online.domain.model.User;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.domain.model.Post;

import java.util.List;

public interface PostService {
    void deletePost(Long postId, User authUser);
    Post updateMyPost(Long postId, PostUpdateRequest postUpdateRequest, User authUser);
}
