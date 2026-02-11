package com.example.online.repository;

import com.example.online.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findPostByIdIn(List<Long> postIds);
}
