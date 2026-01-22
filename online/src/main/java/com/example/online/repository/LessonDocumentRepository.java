package com.example.online.repository;

import com.example.online.domain.model.Lesson;
import com.example.online.domain.model.LessonDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonDocumentRepository extends JpaRepository<LessonDocument, Long> {
    Optional<LessonDocument> findByLessonAndUrl(Lesson lesson, String url);
}
