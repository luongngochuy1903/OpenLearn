package com.example.online.post.service;

import com.example.online.domain.model.Post;
import com.example.online.enumerate.PostOptionChoice;

public interface PostOptionService {
    Post modifyOptionPost(Long postId, PostOptionChoice postOptionChoice);
}
