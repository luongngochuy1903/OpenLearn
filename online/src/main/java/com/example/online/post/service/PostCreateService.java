package com.example.online.post.service;

import com.example.online.domain.model.Post;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;

public interface PostCreateService {
    Post createPost(PostCreateRequest postCreateRequest);
    PostCreateType getType();

}
