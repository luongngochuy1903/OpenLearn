package com.example.online.post.service;

import com.example.online.domain.model.User;
import com.example.online.post.dto.PostGetResponse;

import java.util.List;

public interface PostQueryService {
    PostGetResponse viewPostDetail(Long postId);
    List<PostGetResponse> viewMyPostDetail(User user);
}
