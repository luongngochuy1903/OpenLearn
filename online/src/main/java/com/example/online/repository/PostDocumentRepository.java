package com.example.online.repository;

import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDocumentRepository extends JpaRepository<PostDocument, Long> {
    Optional<PostDocument> findByPostAndUrl(Post post, String url);
    Optional<PostDocument> findByUrl(String url);
}
