package com.example.online.post.service;

import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;

public interface PostCreateService {
    Post createPost(PostCreateRequest postCreateRequest, User authUser);
    Post createPost(Long communityId, PostCreateRequest postCreateRequest, User authUser);
    PostCreateType getType();

}
