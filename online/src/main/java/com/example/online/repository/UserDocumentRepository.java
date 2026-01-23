package com.example.online.repository;

import com.example.online.domain.model.User;
import com.example.online.domain.model.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {
    Optional<UserDocument> findByUserAndUrl(User user, String url);
    Optional<UserDocument> findByUrl(String url);
}
